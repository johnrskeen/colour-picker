package com.skeensystems.colorpicker.ui;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.button.MaterialButton;
import com.skeensystems.colorpicker.R;

public class DropDownButton extends ConstraintLayout {

    // Arrow to indicate whether menu is dropped down or not (also to show the user that extra content exists in the menu)
    public ImageView arrow;
    // Top of drop down menu, this button is the only part that shows when the menu is collapsed
    public Button dropDownButton;
    // Menu content that is displayed when menu is dropped down
    public View dropDownView;

    // Parent scroll view, allows us to set the scroll to top of the dropDownButton when menu is opened
    private ScrollView scrollViewParent;
    // Offset of scroll view parent, allows us to set the scroll to top of the dropDownButton when menu is opened
    private float offset;

    public DropDownButton(Context context) {
        super(context);
        init(context);
    }

    public DropDownButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DropDownButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Set layout to smoothly transition between collapsed and dropped down states
        this.setLayoutTransition(new LayoutTransition());
        this.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        // Set id of layout if it doesn't already have one (needed for constraints)
        if (this.getId() == -1) this.setId(View.generateViewId());

        // Create dropDownButton
        dropDownButton = new MaterialButton(context);
        dropDownButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        // Generate id (needed for constraints)
        dropDownButton.setId(View.generateViewId());
        // Set onClick to toggle drop down status
        dropDownButton.setOnClickListener(view -> toggleLayoutVisibility());
        this.addView(dropDownButton);

        // Create arrow button
        arrow = new ImageView(context);
        // TODO make this drawable customisable
        arrow.setImageResource(R.drawable.anim_arrow_to_up);
        arrow.setElevation(0);
        // Generate id (needed for constraints)
        arrow.setId(View.generateViewId());
        // Set onClick to toggle drop down status
        arrow.setOnClickListener(view -> toggleLayoutVisibility());
        this.addView(arrow);

        // Update constraints, to position views correctly
        updateDropDownButtonConstraints();
    }

    /**
     * Sets scroll view and offset of this layout from the top of the scroll view
     * @param scrollViewParent scroll view this layout is contained in
     * @param offset offset of parent layout of this view from the top of the scroll view
     */
    public void setScrollParams(ScrollView scrollViewParent, float offset) {
        this.scrollViewParent = scrollViewParent;
        this.offset = offset;
    }

    /**
     * Set a custom button as the drop down button (otherwise the default button will be used)
     * @param dropDownButton custom button to use
     */
    public void setDropDownButton(Button dropDownButton) {
        this.removeView(this.dropDownButton);
        dropDownButton.setId(View.generateViewId());

        dropDownButton.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        dropDownButton.setOnClickListener(view -> toggleLayoutVisibility());
        this.addView(dropDownButton);
        this.dropDownButton = dropDownButton;
        updateAllConstraints();
    }

    /**
     * Sets the drop down content to "dropDownView"
     * @param dropDownView the content for the drop down layout
     */
    public void setDropDownView(View dropDownView) {
        if (this.dropDownView != null) throw new IllegalStateException("DropDownButton can only have one view as the drop down content");
        dropDownView.setId(View.generateViewId());
        dropDownView.setVisibility(GONE);

        dropDownView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        this.dropDownView = dropDownView;
        this.addView(dropDownView);
        updateDropDownViewConstraints();
    }

    /**
     * Removes the drop down content from the layout
     */
    public void removeDropDownView() {
        if (dropDownView != null) {
            this.removeViewInLayout(dropDownView);
            dropDownView = null;
        }
    }

    /**
     * Replaces the current drop down content with "newDropDownView"
     * @param newDropDownView the new content for the drop down layout
     */
    public void replaceDropDownView(View newDropDownView) {
        removeDropDownView();
        setDropDownView(newDropDownView);
    }

    private void updateAllConstraints() {
        updateDropDownButtonConstraints();
        updateDropDownViewConstraints();
    }

    /**
     * Constrains dropDownButton to top of layout
     * Constrains arrow to top-right of layout
     */
    private void updateDropDownButtonConstraints() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        // Constrain dropDownButton to top of layout
        constraintSet.connect(dropDownButton.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(dropDownButton.getId(), ConstraintSet.START, this.getId(), ConstraintSet.START, 0);
        constraintSet.connect(dropDownButton.getId(), ConstraintSet.END, this.getId(), ConstraintSet.END, 0);
        // Constrain arrow to top-right of layout
        constraintSet.connect(arrow.getId(), ConstraintSet.TOP, dropDownButton.getId(), ConstraintSet.TOP, 0);
        constraintSet.connect(arrow.getId(), ConstraintSet.BOTTOM, dropDownButton.getId(), ConstraintSet.BOTTOM, 0);
        constraintSet.connect(arrow.getId(), ConstraintSet.END, this.getId(), ConstraintSet.END, 0);
        // Apply constraints
        constraintSet.applyTo(this);
    }

    /**
     * Constrains dropDownView to beneath dropDownButton
     */
    private void updateDropDownViewConstraints() {
        if (dropDownView != null) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(this);
            // Constrain dropDownView to beneath dropDownButton
            constraintSet.connect(dropDownView.getId() ,ConstraintSet.TOP, dropDownButton.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(dropDownView.getId(), ConstraintSet.START, this.getId(), ConstraintSet.START, 0);
            constraintSet.connect(dropDownView.getId(), ConstraintSet.END, this.getId(), ConstraintSet.END, 0);
            // Apply constraints
            constraintSet.applyTo(this);
        }
    }

    /**
     * If dropDownView is visible, make it invisible, otherwise set it to be visible
     */
    private void toggleLayoutVisibility() {
        if (dropDownView == null) return;
        if (dropDownView.getVisibility() == VISIBLE) dropDownView.setVisibility(GONE);
        else {
            dropDownView.setVisibility(VISIBLE);
            // Scroll so that top of dropDownButton is at the top of the scrollViewParent (if set)
            if (scrollViewParent != null) {
                float y = this.getY();
                final Runnable r = () -> scrollViewParent.smoothScrollTo(0, (int) (offset + y - 50));

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(r, 300);//TODO calculate the proper duration
            }
        }
        animateArrow();
    }


    /**
     * Animate the change of state of the arrow
     */
    private void animateArrow() {

        //TODO allow these backgrounds to be customisable
        if (dropDownView.getVisibility() == VISIBLE) {
            arrow.setImageResource(R.drawable.anim_arrow_to_up);//ResourcesCompat.getDrawable(getResources(), R.drawable.anim_star_to_outline, requireContext().getTheme()));
        }
        else {
            arrow.setImageResource(R.drawable.anim_arrow_to_down);//ResourcesCompat.getDrawable(getResources(), R.drawable.anim_star_to_fill, requireContext().getTheme()));
        }
        // Get the animated vector drawable
        AnimatedVectorDrawable avd = (AnimatedVectorDrawable) arrow.getDrawable();
        // Start the animation
        avd.start();
    }
}

/*

use this to set scroll after animation

this.getLayoutTransition().addTransitionListener(new LayoutTransition.TransitionListener() {
    @Override
    public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

    }

    @Override
    public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

    }
});

 */
