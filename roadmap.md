# Colour Picker Roadmap

## Saved Colours page

### Upcoming features
- sharing colours (not sure if this is really needed, copying kind of carries out this function)
- some form of colour groupings defined by the user (i.e. like albums for photos)
- exporting all saved colours in some format
- allow number of columns colours are displayed in to be changed by the user
- view colour database
- some sort of search (don't really know how best to implement this at the moment)
- make inspect colour preview have rounded corners when expanding from rounded corner saved colour button (saved colour buttons are no longer square but are rounded square)
- (introduced in v2.2.0) sometimes favorite colour indicator doesn't show for all colours

### v2.2.1
- (introduced in v2.2.0) fix un-clickable favorite colours bug (after some buttons have been selected)

### v2.2.0
- sorting by colour (maybe offer a few different options of sorting by colour)
- long press to select multiple colours to do stuff with (delete, copy, favorite)

### v2.1.1
- view similar and complementary colour details (drop down view)
- copying colours

### v2.1.0
- colour database (rework this from v1)
- make preview look better, rather than just text on a background
- add support for colours to be viewed in HSL and CMYK
- see similar colours in expanded preview
- see complementary colours in expanded preview

### v2.0.0
- provide functionality from v1

## Camera page

### Upcoming features
- some sort of better preview (i.e. zoom in mode when selecting pixels)

### v2.2.0
- improve smoothness of pixel colour detection
- improve most recent colour info
- make centre colour preview flicker less (especially on status bar - maybe remove this or take step in right direction each time frame)

### v2.0.0
- provide functionality from v1

## Manual Picker page

### Upcoming features
- see colour name when manually picking
- see complementary colours when manually picking
- allow picking by HSL and CMYK

### v2.2.1
- clean up LiveData<String>, this should be LiveData<Integer>

### v2.0.1
- make colour saved notification toast into a snack-bar

### v2.0.0
- provide functionality from v1
- pick by Hex and HSV

## Other

### Upcoming features
- re-add support for multiple languages
- add loading screen while camera is loading (not needed for fast devices but would improve user experience on slower devices)
- add support for RYB, Pantone colours? (and any other colour systems)
- pick from picture in phone (then new bottom navigation: Colour Database - Saved Colours - Camera Picker - Image Picker - Manual Picker, or some other order but this is probably the most appropriate)

### v2.2.1
- re-added support for language region US ("color")

### v2.2.0
- remember user preferences and data (last used sort/filter option + manual picker values)

### v2.0.0
- format tabs nicely
- migrate to database (from old file system), make this only happen on first time old user opens new app (delete old file after migration)
- ads
- new screenshots for google play
- change to use scrolling tabs, could give user choice over which navigation to use in future?

## Comments and Screenshots
- comments version = v2.2.1
- google play screenshots version = v2.1.0