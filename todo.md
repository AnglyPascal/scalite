# TODO List

## Agenda

1. **Complete the Build command**

    - Globals
    - Cleaner
    - Assets
    - Sass
    - Collections

    Should we implement another object that handles rendering all generated pages ?

2. **Test the rendering of Groups' pages**

    Right now there SuperGroup and Group both share the same template, which is
    definition broken.

    The initiation of the Group implementations should be done by the Posts object.
    But then what if someone wants to add a new impl?

3. **Check Posts along with Groups**

    Change a template to show groups and their permalinks. Also test linking to other
    posts. 

4. **Hooks** 
    Some default hooks to finetune some specific behaviour and to check hooks. For
    example, something with Sass.

6. Caching files

7. Create ID for posts, and check if posts can find other posts by that ID

8. Make TagStyle and CategoryStyle into traits, also try to generalize the getPaths
   maybe? We can factor out a few more fields from these two.

9. Found the solution to make Page truly as a Stackable trait. In traits extending
   Page, we first define a general definition of a renderable, then create the final
   defintion by extending it with Page

   Check OtherPage and OtherPost. 


## Rewriting ideas:

1. **Functional, remove mutable states in Standalone objects**
    Change everything to functional style. Remove standalone objects, they linger for
    way too long. Instead create the objects during runtime and pass around the
    references.

2. **Composition over inheritance**
    Page and Reader seem like something that should be composed. Look into it more

3. To change the standalone object style, we'll need to 
    - Write PluginManager as a function, it takes a list of plugin jar files and the
        time they were loaded the last time, and a configuration MObj. It'll return a
        tuple of lists containing all the plugins 


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

3. **Implement Sass handling**

    For now go with dart-sass-java. Figure out how to compile the whole folder into one
    file. Also figure out if it's possible to create separate style files for different
    layouts.


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

- **Mustache predefined lambdas**

    Add filters from jekyll in the layouts.helpers module.
    Make this into a pluginable trait

