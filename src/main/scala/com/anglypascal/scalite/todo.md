List of Things to do now

- Write handler for context specific to collections

- Api for custom behaviour with custom entries in the front matter. 
    Make an api for the post or page or whatever that lets users add specific effects
    to the tags by providing a `() => String` function

- Implement writers in the command pattern. Inside writers module there need to be a
- file cleaner Make a cache system

- Need to add the logger

- Write the main Site object? Or class? 
    Decide how to go on about that global influence

- Front matter documentation. 
    The way to do this is to give an api for the front matter in a separate file like
    Global

- Command system, support for build, new, serve, clean

- Api for a custom tag. 
    For example, if someone wants to output the render time of the post, they will
    define an extension to the mustache helper (give it a different name) and simply
    define a function with the name `render()` and the output will be returnd by this
    funciton. More functionality can obviously be achieved by exploiting the behaviour
    of mustache library

- Plugin class, that provides a public api to add plugins. 
    Add support for plugins, converters, and such by adding a function in the
    converters class that lets users to add a plugin without editing the source file.
    Define the "addStuff" method

- Theme handler
    Write class for handling themes, locally specified theme for layouts and pages.

- Cacher. 
    Will store metadata about posts, so that we don't render things already rendered.
    Also store cached data of things.

    Testing

- How do I do testing??

- String processors :
    need to write the slugify and prettify functions. 
