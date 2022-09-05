# TODO List

## Agenda

1. **Implement Sass handling**

    For now go with dart-sass-java. Figure out how to compile the whole folder into one
    file. Also figure out if it's possible to create separate style files for different
    layouts.

2. **Test the rendering of Groups' pages**

    Right now there SuperGroup and Group both share the same template, which is
    definition broken.

    The initiation of the Group implementations should be done by the Posts object.
    But then what if someone wants to add a new impl?

3. **Check Posts along with Groups**

    Change a template to show groups and their permalinks. Also test linking to other
    posts. 

4. **Complete the Build command**

    - Globals
    - Cleaner
    - Assets
    - Sass
    - Collections

    Should we implement another object that handles rendering all generated pages ?


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
2. **Defaults in Assets**

    Add the defaults configuration to Assets

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

    Add filters from jekyll in the layouts.helpers module.
    Make this into a pluginable trait

- **Hooks** 
    Fine grained control. These will be done much later. Hooks can be added via mixins.
    There will be a Hooks object with all the hooks available. A Hook class will be
    provided with a single method call(). Now in places where Hooks need to be called,
    all the Hook objects in that array in Hooks will be called sequentially. 
