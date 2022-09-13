Scalite
-------

[Under development]

Scalite is a fully customizable blog-aware static site generator in Scala 3. 

Inspired from [Jekyll][jekyll], Scalite takes customizability on a different level. It
works out of the box, it supports customization to its default behaviour through simple
YAML configuration files, and each of its functionality can be extended with simple
plugins.

## Features

### Generic

Scalite doesn't assume whether you're making a blog for yourself or a homepage for a
company, it's made to support any kind of static site. 

By default Scalite provides three kind of user-written contents called Elements:

Type     | Description
---------|-------------
ItemLike | Anything that won't be rendered as a separate webpage, but will be used as
         | part of other contents.
---------|-------------
PageLike | Contents that will be rendered as a webpage, but unlike PostLike contents,
         | doesn't have groups metadata.
---------|-------------
PostLike | Blog-like posts with the functionality of groups. A post can belong to
         | different groups. By default, two kinds of groups are defined: tags and
         | categories. 

Besides, there's a plugin api that supports the definition of a custom Element.

### Language-agnostic

Scalite doesn't force a templating language on you. By default Scalite provides support
for [Mustache][mustache] templates, but plugins can be written to handle any other
templating languages. Better yet, multiple templating languages can be used together in
a single project.

[Markdown][markdown] is supported by default, but you can use any markup language to
write your content, so long as you provide a plugin for it. 

Scalite supports [Sass][sass] stylesheets. If you fancy other contents that needs to be
converted to something else, it's as simple as providing a converter, and adding a few
lines in the YAML configuration!

### No fixed directory structure

You don't need to put your content in any of the default specified directories, add a
line in the configuration, and the new directories will be used.

You don't need different bundles of content to be in different folders. Contents can
have meta data in its front matter to set which groups they should be in. For example,
to create a collection of pages related to programming, where pages might be collected
by programming languages, you can put the posts into respective folders:

```
programming/
    scala/ 
        post1.md
        post2.md
    python/
        post3.md
        post4.md
```
Or, you could define a new `SuperGroup` named "programming" and add the following in
the front matter of any page `post.md`:
``` yaml
programming: scala, python
```
And the post would be placed into a new hierarchy structure as given by the previous
example, no matter where the source file `post.md` resides.



[jekyll]: https://jekyllrb.com/
[mustache]: https://mustache.github.io/
[markdown]: https://www.markdownguide.org/
[sass]: https://sass-lang.com/
