# Post

``` yaml
site: 
  globals: _*
  collection: 
    posts: _*

page:
  title: # name of the post
  date: # date in the format yyyy-MM-dd HH:mm:ss
  modifiedTime: # last modified time
  visible: # should this post be rendered
  tags: # space separated list of tags
  categories: # comma separated list of categories
  outputExt: # extension of the output file
  url: # permanent url to this post page

```
``` yaml
globals:
```

Remove the collection object from globals, and add it to collection themselves, then
pass the locals of collections to the posts?

``` yaml
collections:


```
