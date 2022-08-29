# TODO List

## URGENT

- Write the string handlers:
    - `excerpt()`: creates an excerpt of the post. Copy jekyll for now.

- Start with global settings handling. Write the specification for items
    - We don't need many options to handle posts. Then check the code for posts, and
        test a small case.
    - Check url creation, that it words and the partials work.

- Write a Main object, with only one option for now: `build`. 

- On the meantime, take care of sass rendering. I don't know how themeing will work, so
    need to take a closer look.
    - I think it's just compile everything in the `_sass` folder, and put everything
        inside `/assets/css/style.css` file

- Front matter documentation, and other documentations. Work on this as you go.



## Less priority

- Generators: takes charge of automatically generated pages.



## Feature Options

- Emulate liquid filters for mustache using lambdas. Provide built in support for
    Handlebars and Liquid maybe?

- Give a API that allows a custom function mapped to a placeholder with signature maybe 
    ``` scala
    () => String
    String => String
    ```
    which will be used when `{{function_key}}` is used in mustache layouts.

    Check if lambda's can be added to the Data object so that it gets rendered at the
    same time as other keys, without having to implement mustachehelper support

- We can also allow for custom placeholder classes to be passed to the renderer via the
    MustacheHelper trait. 

    For example, if someone wants to output the render time of the post, they will
    define an extension to the mustache helper (give it a different name) and simply
    define a function with the name `render()` and the output will be returnd by this
    funciton. More functionality can obviously be achieved by exploiting the behaviour
    of mustache library

- Localized theming for layouts? Is it possible?

- Hooks: fine grained control. These will be done much later
    Hooks can be added via mixinx. There will be a Hooks object with all the hooks
    available. A Hook class will be provided with a single method call(). Now in places
    where Hooks need to be called, all the Hook objects in that array in Hooks will be
    called sequentially. 
