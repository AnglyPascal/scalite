# TODO List

## Context Handler

- Go around checking the model. Adding logs while you're at it. Attention:
    - Effects of the immutable Data
    - Before everything, make sure that the abstraction with collection works

- Write the string handlers:
    - How are we using `slugify()` and `titlifiy()`?
    - `excerpt()`: creates an excerpt of the post. Copy jekyll for now.

- Start with global settings handling. Write the specification for items
    - We don't need many options to handle posts. Then check the code for posts, and
        test a small case.
    - Check url creation, that it words and the partials work.

- Collections ToC page

- Make a directory builder and file writer
    - Inside collection items, define the output path. It should be defined by the url.
    - Write a file copier that copies static files.
    - While you're at it, look into the caching system.

- Write a Main object, with only one option for now: `build`. 

- On the meantime, take care of sass rendering. I don't know how themeing will work, so
    need to take a closer look.
    - I think it's just compile everything in the `_sass` folder, and put everything
        inside `/assets/css/style.css` file

- Front matter documentation, and other documentations. Work on this as you go.


## A bit later

- Give a API that allows a custom function mapped to a placeholder with signature maybe 
    ``` scala
    () => String
    String => String
    ```
    which will be used when `{{function_key}}` is used in mustache layouts.

- We can also allow for custom placeholder classes to be passed to the renderer via the
    MustacheHelper trait. 

    For example, if someone wants to output the render time of the post, they will
    define an extension to the mustache helper (give it a different name) and simply
    define a function with the name `render()` and the output will be returnd by this
    funciton. More functionality can obviously be achieved by exploiting the behaviour
    of mustache library

- Write a public API named Plugin that will handle the object fetching from the
    `_plugins` directory by adding them to the classpath. Also it will handle the
    object initialization so that they are properly processed. Will need an API for
    this as well.

- Plugins: 
    [This answer seems very promising](https://stackoverflow.com/questions/23874281/scala-how-to-compile-code-from-an-external-file-at-runtime)
- Localized theming for layouts? Is it possible?

- Generators: takes charge of automatically generated pages.

- Hooks: fine grained control. These will be done much later
    Hooks can be added via mixinx. There will be a Hooks object with all the hooks
    available. A Hook class will be provided with a single method call(). Now in places
    where Hooks need to be called, all the Hook objects in that array in Hooks will be
    called sequentially. 
