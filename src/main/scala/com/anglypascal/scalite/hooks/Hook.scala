package com.anglypascal.scalite.hooks

trait Hook:
  def apply(): Unit 

trait ConvertHooks extends Hook 

trait CollectionHooks extends Hook

trait LayoutHooks extends Hook 

trait SiteHooks extends Hook
