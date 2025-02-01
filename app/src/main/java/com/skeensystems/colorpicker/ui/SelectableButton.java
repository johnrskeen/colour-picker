package com.skeensystems.colorpicker.ui;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SelectableButton extends ConstraintLayout {

    // This button takes the colour and provides the onClick functions (where the program sets the onClick attribute)
    // Possibly not needed but there were problems with SelectableButtons losing their onClick ability after being selected (I think this has now been fixed but not 100% sure what was the cause)
    // It was after being set to not VISIBLE, but I don't see why that would cause the problems it was causing
    public Button colour;
    // TextView displaying a tick if the button is selected
    private TextView tick;
    // TextView indicating if the colour is a favorite colour (drawable set by the caller)
    private TextView favoriteIcon;
    // Radius of corners when not selected, defaults to 0
    private float unselectedCornerRadius = 0;

    // True if layout has been drawn and favoriteIcon has been sized correctly
    private boolean readyForVisibleStar;
    // Indicates whether an attempt was made to set favoriteIcon before it was ready (so we know to set favoriteIcon appropriately when it is ready)
    private boolean needVisibleStar;

    // If this button is currently selected
    public boolean selected = false;

    // Duration of scaling and corner radius changing animations
    private int animationDuration = 0;

    public SelectableButton(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SelectableButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SelectableButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Set layout to animate changes (currently not using this as when multiple buttons were selected in quick succession, it didn't look very smooth)
     * Creates colour button and tick and favoriteIcon text views and adds to layout
     * @param context needed for creation of the views
     */
    private void init(Context context) {
        // Attempting to stop growing in of star from edge
        // Doesn't work perfectly at the moment
        readyForVisibleStar = false;

        // Set layout to animate changes (CURRENTLY NOT USING)
        //this.setLayoutTransition(new LayoutTransition());
        //this.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        // If layout has no id, generate one for it (needed for applying constraints)
        if (this.getId() == -1) this.setId(View.generateViewId());

        // Create colour button
        colour = new Button(context);
        // Generate id (needed for applying constraints)
        colour.setId(View.generateViewId());
        colour.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        colour.setVisibility(VISIBLE);
        // Set elevation to 0, so this is above tick view
        colour.setElevation(0);
        this.addView(colour);

        // Create tick text view
        tick = new TextView(context);
        // Generate id (needed for applying constraints)
        tick.setId(View.generateViewId());
        tick.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        tick.setVisibility(INVISIBLE);
        // Set elevation to 100, so this is above colour button
        tick.setElevation(100);
        this.addView(tick);

        // Layout listener for this layout, so we know when it has been drawn, enabling us to calculate favoriteIcon dimensions
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove listener
                getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Set width and height to 1/3.5 * parent dimensions
                ConstraintLayout.LayoutParams params = (LayoutParams) favoriteIcon.getLayoutParams();
                params.width = (int) (getWidth() / 3.5);
                params.height = (int) (getHeight() / 3.5);
                favoriteIcon.setLayoutParams(params);

                // Make favoriteIcon visible if needed
                if (needVisibleStar) {
                    favoriteIcon.setVisibility(VISIBLE);
                    needVisibleStar = false;
                }
                // Indicate we are ready for a visible favoriteIcon
                readyForVisibleStar = true;
            }
        };
        this.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

        // Create favoriteIcon text view
        favoriteIcon = new TextView(context);
        // Generate id (needed for applying constraints)
        favoriteIcon.setId(View.generateViewId());
        favoriteIcon.setVisibility(INVISIBLE);
        // Set elevation to 100, so this is above colour button
        favoriteIcon.setElevation(100);
        // Set width and height to 0 for now, until we can calculate desired params (after parent has been drawn)
        favoriteIcon.setLayoutParams(new ConstraintLayout.LayoutParams(0, 0));

        this.addView(favoriteIcon);
    }

    /**
     * Sets duration of scaling and corner radius changing animations
     * @param animationDuration duration to use
     */
    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    /**
     * Sets visibility of favoriteIcon to show if colour is favorite, or to not show if colour is not favorite
     * @param favorite true = favorite colour, false = not favorite colour
     */
    public void updateFavorite(boolean favorite) {
        if (favorite && readyForVisibleStar) favoriteIcon.setVisibility(VISIBLE);
        else if (favorite) needVisibleStar = true;
        else favoriteIcon.setVisibility(INVISIBLE);
    }

    /**
     * Set corner radius of button when not selected
     * @param unselectedCornerRadius corner radius in px (should match corner radius of drawable for smooth transition between selected and unselected states)
     */
    public void setUnselectedCornerRadius(float unselectedCornerRadius) {
        this.unselectedCornerRadius = unselectedCornerRadius;
    }

    /**
     * Set drawable of tick button
     * @param background drawable to set to the tick button
     */
    public void setTickBackground(Drawable background) {
        tick.setBackground(background);
    }

    /**
     * Set drawable of favorite indicator button
     * @param background drawable to set to the favorite indicator button
     */
    public void setFavoriteIndicatorBackground(Drawable background) {
        favoriteIcon.setBackground(background);
    }

    /**
     * Set background tint of tick and favoriteIcon views
     * @param colour tint colour for tick = Color.argb(200, Color.red(colour), Color.green(colour), Color.blue(colour))
     *               tint colour for favoriteIcon = Color.argb(255, Color.red(colour), Color.green(colour), Color.blue(colour))
     *               alpha for tick is 200, alpha for favoriteIcon is 255
     */
    public void setBackgroundTint(int colour) {
        ColorStateList tickColour = new ColorStateList(new int[][]{{}}, new int[]{Color.argb(200, Color.red(colour), Color.green(colour), Color.blue(colour))});
        tick.setBackgroundTintList(tickColour);
        ColorStateList favoriteColour = new ColorStateList(new int[][]{{}}, new int[]{Color.argb(255, Color.red(colour), Color.green(colour), Color.blue(colour))});
        favoriteIcon.setBackgroundTintList(favoriteColour);
    }

    /**
     * Provides visual feedback that the button has been selected by shrinking and adding corner radius
     */
    public void select() {
        // Make tick visible
        tick.setVisibility(VISIBLE);
        // Set selected to true
        selected = true;
        // Shrink layout to indicate it is selected
        animateSize(1.0f, 0.8f, animationDuration);
        // Animate corner radius to be 0.25 * height
        animateCornerRadius(unselectedCornerRadius, this.getHeight() / 4.0f, animationDuration);
    }

    /**
     * Provides visual feedback that the button has been deselected by growing and removing corner radius
     */
    public void deselect() {
        // Set selected to false
        selected = false;
        // Grow layout back to original size to indicate it is not selected
        animateSize(0.8f, 1.0f, animationDuration);
        // Animate corner radius back to default value
        animateCornerRadius(this.getHeight() / 4.0f, unselectedCornerRadius, animationDuration);
        // Make tick invisible
        tick.setVisibility(INVISIBLE);
    }


    /**
     * Animates change of scale
     * @param valueFrom initial scale size
     * @param valueTo final scale size
     * @param animationDuration duration of the animation
     */
    private void animateSize(float valueFrom, float valueTo, int animationDuration) {
        // Animation to change size of layout to indicate it is selected/not selected
        ScaleAnimation scaleAnimation = new ScaleAnimation(valueFrom, valueTo, valueFrom, valueTo,
                Animation.ABSOLUTE, this.getX() + this.getWidth() / 2.0f,
                Animation.ABSOLUTE, this.getY() + this.getHeight() / 2.0f);
        scaleAnimation.setDuration(animationDuration);
        scaleAnimation.setFillAfter(true);
        // Start the animation
        this.startAnimation(scaleAnimation);
    }

    /**
     * Animates change of corner radius
     * @param valueFrom initial corner radius
     * @param valueTo final corner radius
     * @param animationDuration duration of the animation
     * @throws ClassCastException button background type must be castable to GradientDrawable
     */
    private void animateCornerRadius(float valueFrom, float valueTo, int animationDuration) {
        // If background of colour button is not a gradient drawable, we cannot change it's corner radius, so throw an error
        try {
            // Get background of colour button
            GradientDrawable gradientDrawable = (GradientDrawable) colour.getBackground();
            // Create animator to change corner radius
            ValueAnimator animator = ValueAnimator.ofFloat(valueFrom, valueTo);
            animator.setDuration(animationDuration)
                    .addUpdateListener(animation -> {
                        float value = (float) animation.getAnimatedValue();
                        gradientDrawable.setCornerRadius(value);
                    });
            // Start the animation
            animator.start();
        }
        catch (ClassCastException e) {
            // Provide information to the user on why SelectableButton needs gradient drawable for selection features
            throw new ClassCastException("SelectableButton must have a gradient drawable to use .select() and .deselect()");
        }
    }
}
