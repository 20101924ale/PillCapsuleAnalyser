# Pill Capsule Analyser

Data Structures & Algorithms - Semester 4 Assignment 1

![first](https://cdn.discordapp.com/attachments/656269411879092247/1284077141386072106/Screenshot_2024-09-13_at_09.48.37.png?ex=66e55187&is=66e40007&hm=a6364ae8dbf61750226ff8f7d2ea7ddff537e9e4bd79eb23c6c546efadfc87b4&)

- The Pill Capsule Analyser app allows users to upload and analyze images of pills through an interactive GUI as shown above. The app converts images to black and white by reading each pixel’s color values and adjusting them based on a threshold. It also enables users to adjust the image’s hue, saturation, and brightness in real-time using sliders. The app displays basic image metadata, such as file name and size, and uses a Union-Find algorithm to group connected pixels (based on color similarity), which are then recolored with random colors. This enables advanced image segmentation, making it easier to isolate and analyze different regions of the image.

  ![snd](https://cdn.discordapp.com/attachments/656269411879092247/1284077140953927690/Screenshot_2024-09-13_at_09.49.03.png?ex=66e55187&is=66e40007&hm=c9c5e1c6fa4abd588a364933d9f8e8f40f38a7befde84bd1163b23639800cc36&)

- This is the black and white conversion once a pill is clicked anywhere in the image. 
When a pill image is clicked, the app converts it to black and white by reading each pixel's color values. It calculates the brightness of each pixel, and if the brightness is above a certain threshold, the pixel is turned white; otherwise, it is turned black. This process effectively simplifies the image, making it easier to distinguish different regions by removing color and focusing on intensity.

  ![third](https://cdn.discordapp.com/attachments/656269411879092247/1284077139804557342/Screenshot_2024-09-13_at_09.49.53.png?ex=66e55187&is=66e40007&hm=f0fc7bfa5c51b802a4f7ebd091a90b3ae69999b86df8ee5c1ba2dbd778d4c04b&)

- The color sampling feature in the app allows users to sample and modify colors from an image. When a pixel is selected, the app captures the color at that specific point and retrieves its RGB (Red, Green, Blue) values. These values can then be used to apply color adjustments, such as hue, saturation, and brightness changes, through sliders. The sampled color can also be used for segmentation, where connected pixels with similar colors are grouped together and recolored, enabling more detailed analysis of specific areas within the image.
