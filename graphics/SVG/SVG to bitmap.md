## Transforming SVGs into bitmap images (e.g. PNGs)

1. Download Inkscape: http://www.inkscape.org/download/

2. Open the SVG file you want to transform
3. Hit `Ctrl+A` to select everything
4. Hit `Ctrl+Shift+M` to bring up the "Transform" panel, and select the "Scale" tab
5. Check the "Scale proportionally" checkbox
6. Select "px" from the list of units
7. If the current width is greater than the height, set the width to your desired size; otherwise set the height
8. Click "Apply"
9. Hit `Ctrl+Shift+D` to bring up the "Document properties" window, and expand the "Resize page to content" part under "Custom size"
10. Click on "Resize page to drawing or selection"
11. Now you can see the current size of the object. If you need margins, add them and click on the button again
12. Close the "Document properties" window
13. Hit `Ctrl+Shift+E` to bring up the "Export Bitmap" window, and select the "Page" option
14. Set the file name, and click on "Export"
15. Close *without saving*

It is *strongly* recommended that you set integer margins, even if that means having different margins on two sides. For instance, if you want a 100x100 picture but the image is 81 pixels wide, set the left margin to 9.00 and the right margin to 10.00 rather than both to 9.50.  
This is because Inkscape will render points that are "between" pixels in each pixel it touches with a diluted color, resulting in a blurry image, e.g. a white line from 0.5,0.5 to 1.5,0.5 will be rendered as a 3x2 gray block whereas a line from 0,0 to 1,0 will be a 2x1 white line.

### Changing the color of an SVG

1. Open your SVG file
2. Hit `Ctrl+Shift+F` to bring up the "Fill and Stroke" panel
3. For each object in the file (usually 1 or 2, each a different color):
3.1 Click on the object
3.2 Set its RGB color in the "Fill and Stroke" panel (*don't touch the Alpha!*) to the one you want
4. Hit `Ctrl+Shift+D` to bring up the "Document properties" window
5. Click on the color next to "Background", and set it to the background you want (usually 0 alpha for transparent)
6. You can now export your SVG with the right color