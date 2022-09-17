/** There will be entry in the frontMatter:
  *
  * ```
  * GroupName:
  *   - branch1
  *     - leaf1
  *     - leaf2
  *   - branch2
  *     - branch3
  *       - leaf 3
  *     - branch4
  *       - leaf 4
  * ```
  *
  * And the Renderable with be inside the nested directories and will have the
  * same url pattern, and the taxonomical pages will be navigatable in that
  * order
  */

/** Also, users might want to give index.md files for the groups. how are they
  * going to do that? for example, there might be two cats: cat1, cat2, and they
  * are in a nested order cat1/cat2/... then the user might want to render stuff
  * in pages /cat1 and /cat1/cat2 as well.
  */
