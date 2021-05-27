# DigitalMedia_littlePhotoShop
1) Correct the overflow that occurs when changing the brightness.

2) Next, do a colour transformation from RGB to YUV.

The formula is:

Y = 0.299 * R + 0.587 * G + 0.114 * B
U = (B - Y) * 0.493
V = (R - Y) * 0.877

The inverse transformation is:

R = Y + V/0.877
G = 1/0.587 * Y - 0.299/0.587*R - 0.114/0.587 * B
B = Y + U/0.493

3) Next, implement the following:

  a) change of brightness (-128 to 128)

  b) change of contrast (0 to 10.0)

interval: [0, 0.2, 0.4, 0.6, 0.8, 1, 2, 4, 6, 8, 10]

  c) change of saturation (0 to 5.0)

interval: [0, 0.25, 0.5, 0.75, 1, 2, 3, 4, 5]

  d) change of colour (Hue) 
