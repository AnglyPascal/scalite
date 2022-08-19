# Documentation about the configuration options

## Global configuration

This is done inside `/_config.yml` file. These configurations define the global
behaviour of the library. These can be overriden in front matters of posts and
other objects. 

### Directory defaults

These need to be added under the **directories** section
```yaml
directories:
  baseDir: /src/main/scala/site_template # where source files are
  destination: /_site     # path relative to baseDir where the generated site will be
  layoutsDir: /_layouts   # path to the layouts
  collectionsDir: baseDir # root path to the collections
```

### Converter file extensions defaults

These need to be added under the **coverters.extensions** section

- **markdownExt**: to modify the file extensions the markdown converter processes
  _defaults_: `"markdown,mkdown,mkdn,mkd,md"`
- **convExt**: to modify the file extensions the custom converter with filetype
    _conv_ processes
  _defaults_: `""`


### Collections

The collections section will be hold configurations for collection items. Defaults:
```yaml
collections: 
  # overrides the one in the directories section.
  collectionsDir: baseDir 
  posts:
    output: true       # render posts?
    sortBy: date       # sort by this property
    folder: /_posts    # folder where posts will be
    toc: false         # table of contents for posts
    directory: collectionsDir # directory where folder will be

  drafts: false # single entries like this will be handle with the defaults
  # alternatively,
  drafts:
    output: false

  new_collection:
    output: false   # don't render items by default
    sortBy: title   # sortBy title by default
    folder: /_new_collection # path naming convention
    toc: false      # don't render table of contents page
    directory: collectionsDir # directory where folder will be
```
Any other entry to the collections section will create a custom GenericItems object
containing GenericItem's representing the elements of the collection. For example, the
elements of the `new_collection` will be searched for in the path `directory + folder`. 

For now, setting `output: false` will completely turn off the rendering of that
collection. Later, we could add `cache: true` option, which unless specified otherwise,
cache the posts even when output is set to false.


The front-matter defaults should be placed inside Globals: defaults. Scope
option will define the scope of these defaults.


#### Post front_matter

In the front matter, the following entries are standard:
``` yaml
title: null             # name of the post
date: null              # date in the format yyyy-MM-dd HH:mm:ss
visible: false          # should this post be rendered
tags: null              # space separated list of tags
categories: null        # comma separated list of categories
outputExt: ".html"      # extension of the output file
date_format: yyyy-MM-dd # format in which to rendered the date in the output
permalink: "/{{>pretty}}" # template for the permalink 
```
