# Documentation about the configuration options

## Global configuration

This is done inside `/_config.yml` file. These configurations define the global
behaviour of the library. These can be overriden in front matters of posts and
other objects. 

### Directory defaults

- **destination**: Where the rendered files will be stored. 
  _default_: `/_site`
- __base__: The directory containing the files for the site.
  _default_: `/src/main/scala/site_template`
- __layout_dir__: Directory containing layout files.
  _default_: `/_layouts`
- __post_dir__: Directory containing post files.
  _default_: `/_layouts`


The front-matter defaults should be placed inside Globals: defaults. Scope
option will define the scope of these defaults.
