# Image

A wrapper around an `<img>` element that will run the URLs through the image service and swap in an alternate image if the primary results in an error.


Property | Description
---------|----------
`@Input() imageUrl: string`  | URL for the primary image
`@Input() altImageUrl: string` | URL for the alternate image
`@Input() altText: string` | Text to show if neither image is found