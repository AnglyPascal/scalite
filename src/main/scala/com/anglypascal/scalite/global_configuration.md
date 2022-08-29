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


#### Post frontMatter

In the front matter, the following entries are standard:
``` yaml
title: null             # name of the post
date: null              # date in the format yyyy-MM-dd HH:mm:ss
visible: false          # should this post be rendered
tags: null              # space separated list of tags
categories: null        # comma separated list of categories
outputExt: ".html"      # extension of the output file
dateFormat: yyyy-MM-dd # format in which to rendered the date in the output
permalinkTemplate: "/{{>pretty}}" # template for the permalink 
```


## Permalink

A permalink template is a mustache template. To set a new permalink in the the configs,
remember to use quotations so that yaml recognizes the `{{}}` as strings. The following
tags are allowed in permalink templates:

Variable     | Description
------------ | ------------
`year`         | Year from posts filename or front mater date
`collection`   | The collection this post belongs to
`categories`   | The specified categories for this post. If a post has multiple categories, Jekyll will create a hierarchy (e.g. /category1/category2). Also Jekyll automatically parses out double slashes in the URLs, so if no categories are present, it will ignore this.
`tags`         | The tags this post has, separated by '-'
`title`        | Title of this post, not slugified
`slug`         | slugified, all lower case, escapes all non alphabetical characters
`prettySlug`   | slugified, doesn't escape a few standard non alphabetical chars
`casedSlug`    | slugified, case sensitive, escapes alphabetical chars
`asciiSlug`    | slugified, url encoded
`outputExt`    | extension of this file
`modifiedTime` | last modified time of this post/page
