# Structure of the base folder 

``` css
.
├── about.md          /* markdown files with yaml headers */
├── index.md
├── config.yml        /* yaml file to provide site wide settings */
├── _includes         /* static template files that will be included in each page */
│   ├── footer.html
│   ├── header.html
│   ├── custom_head.html /* optional, to provide extra head stuff */
│   └── head.html
├── _layouts          /* layout files, can be created new layouts, can be nested */
│   ├── default.html
│   ├── main.html
│   ├── page.html
│   └── post.html
├── _posts            /* posts with yyyy-mm-dd-title.md formatd filename */
│   ├── 2016-05-19-super-short-article.md
│   └── 2016-05-20-my-example-post.md
├── _sass             /* style files used in the template */
├── assets            /* any assets used */
├── scripts           /* any scripts used */
└── site              /* rendered site appears here */
```


# What happens when run "template directory" is hit

1. reads off configs from `config.yml` and adds it to `Globals` 
2. creates Value objects for `site` and `page`
3. creates a `Layout` object for each `layout in _layouts`
4. creates a `Post` object for each `post in _posts` with `visible = true`
5. figures out the permanent links
6. renders `index.html` with the list of posts
7. renders `about.html`
8. renders each `Post` object
9. creates new folder `./site/` to hold the entire site
10. writes everything to appropriate folders inside the base folder


# Rendering layouts

1. Read each layout file, making connections connections between layouts 
2. Could use the partials in mustache here, or
3. Could just do it statically

# Rendering posts

1. After connections between layouts are established, 
2. post rendering is just passing the json object to the templates
3. here need to figure out what the fields of the object will be

``` json
{
  "site": { 
    "title" : "site title",
    "lang"  : "en",
    "paginate" : false,
    "show_excerpts": true,
    "root_url": "/",
    "has_title": true,
    "author": {
      "name": "author name",
      "email": "author email"
    },
    "description": "site description"
  },
  "content": "that needs to be added to this page",
  "page": {
    "title": "page title",
    "list_title": "title of the list, only for the index page"
  },
  "head": "head part of the page",
  "header": "the header that needs to be included in this page",
  "footer": "the footer that needs to be included in this page",
  "posts": [
    { "date" : "post date", 
      "title": "post title", 
      "url": "post url", 
      "excerpt": "something",
      "modified_date": "modified date"
    },
    { "date" : "post date", 
      "title": "post title", 
      "url": "post url", 
      "excerpt": "something",
      "modified_date": "modified date"
    },
    { "date" : "post date", 
      "title": "post title", 
      "url": "post url", 
      "excerpt": "something",
      "modified_date": "modified date, only when rendering post page",
      "authors": []
    },
  "custom_head" : "if need be, for head.html"
  ],
  "social": "for include in footer"
}
```
