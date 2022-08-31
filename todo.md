# TODO List

## Agenda

1. **Complete implementation of Groups:**

    Group
      - Has a type like Tags or Categories
      - This is where we can add extensibility. Should all group be similar?

2. **Test the rendering of Groups' pages**

    The initiation of the Group implementations should be done by the Posts object.
    But then what if someone wants to add a new impl?

3. **Check Posts along with Groups**

    Change a template to show groups and their permalinks. Also test linking to other
    posts. 

4. **Test Collection pages**

    Check if all posts are found properly, if the toc page needs any more information.

5. **Complete the Build command**

    - Globals
    - Cleaner
    - Assets
    - Sass
    - Collections
    - Generated Pages

    Should we implement another object that handles rendering all generated pages ?

6. **Implement Sass handling**

    For now go with dart-sass-java. Figure out how to compile the whole folder into one
    file. Also figure out if it's possible to create separate style files for different
    layouts.

7. **Defaults in Assets**

    Add the defaults configuration to Assets

## Finished

1. **Collection style**

    Write several different styles a collection can have. For now: PostItem, PageItem,
    ListItem. These can be selected in the config setting under collections.name.style.
    Posts, Drafts are impl of PostItem, StaticPages impl PageItem, GenericItem impls
    ListItem.

    Collections will keep a map of (styleName, style), and new implementations will
    provide a new style basically.

    ``` yaml
    collections:
        posts:
            style: post
            folder: /_posts
            output: true
        docs:
            style: post
            folder: /_documents
            output: true
        employees:
            style: item
            folder: /employees
            output: false
    ```

## Documentation

1. **Skeleton of the site**

    Create a skeleton of the website, and test it before proceeding to write
    documentation

2. **Overview**

    Start with an overview of how this generator works. Basically the homepage.

3. **URGENT: Configuration documentation** 

    Document these as soon as possible before I forget myself.

4. **Brief overview of Mustache**

5. **Documentation for plugins**


## Feature Options

- **Excerpt**
    Creates an excerpt of the post. Add more functionality, customization.

- **Mustache predefined lambdas**
    Emulate liquid filters for mustache using lambdas. Provide built in support for
    Handlebars and Liquid maybe? These are inspired from filters from jekyll.

    Give a API that allows a custom function mapped to a placeholder with signature maybe 
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

- **Hooks** 
    Fine grained control. These will be done much later. Hooks can be added via mixins.
    There will be a Hooks object with all the hooks available. A Hook class will be
    provided with a single method call(). Now in places where Hooks need to be called,
    all the Hook objects in that array in Hooks will be called sequentially. 