# Arcane

This mod exposes more of Minecraft's enchanting power system to users by making elements of it more data driven.  It is geared towards data pack and mod pack developers alike, though mod developers may also find it useful.

In vanilla Minecraft, you can add entries to the `enchantment_power_provider` tag to make them provide power to enchanting tables like bookshelves do (in fact, bookshelves themselves are part of this tag by default).  However, this power boost is hard coded to 1 (the same as bookshelves), and it doesn't respect block states, making it inconvenient for certain block types (like candles, which can contain multiple candles in one block space). This can sometimes be - to put it rather bluntly - frustrating.

Arcane offers a solution by adding **data driven enchanting power providers**, which can be much more flexible in their parameters.

## Compatibility:
Arcane does not interact in any way with vanilla nor modded enchanting power providers.  This decision was made to maintain mod compatibility first and foremost.  In addition, the additions to base code are simple and minimal.  However, there are a few areas where Arcane may run into compatibility issues, in particular with:
- Mods that affect the enchanting table's detection radius.  Arcane mainly relies on vanilla functionality for this, so it may place nicely depending on how responsibly this change was implemented.  However, an increased radius may result in some funny business concerning obstruction detection due to how it is calculated (a copy of vanilla's detection system).
- Mods that completely throw out the vanilla enchanting system.  It should be obvious why compatibility issues would arise here.

## For Data Pack Developers:
Your own enchanting power providers should be put in the `arcane/enchantment_power_provider` directory in the namespace folder in your data pack. For instance, a datapack called `more_magical_blocks` would have its enchanting power providers in `data/more_magical_blocks/arcane/enchantment_power_provider`.  The files themselves are formatted as JSON files that look something like this:
```json5
{
  "type": "namespaced:type_goes_here",
  "provider_blocks": "#block_tag:goes_here"
  // extra parameters go here
}
```
The parameters the provider accepts depends on its type.  Two are included with the base mod: 
- `arcane:constant` supplies a constant boost to the enchanting table determined by the `amount` field.

    Example:
```json5
{
  "type": "arcane:constant",
  "amount": 1,
  "provider_blocks": "#arcane_test:provides_constant_1_power"
}
```
- `arcane:block_state` lets you determine power from state properties in affected blocks through the `providers` field. This contains an array of objects, each with their own `states` field and `amount` field.  `states` is also an array which can contain multiple objects, but this time each object contains block states which must be satisfied in order to supply its corresponding `amount` of power.  Finally, the optional `additive` field determines if each provider stacks cumulatively rather than only applying the boost from the first provider they satisfy.  By default, this is set to false.

    Examples:
```json5
{
  "type": "arcane:block_state",
  "providers": [
    {
      "states": [
        {
            "candles": "1",
            "lit": "true"
        }
      ],
      "amount": 0.25
    },
    {
      "states": [
        {
            "candles": "2",
            "lit": "true"
        }
      ],
      "amount": 0.5
    },
    {
      "states": [
        {
            "candles": "3",
            "lit": "true"
        }
      ],
      "amount": 0.75
    },
    {
      "states": [
        {
            "candles": "4",
            "lit": "true"
        }
      ],
      "amount": 1
    }
  ],
  "provider_blocks": "#arcane_test:candles_for_enchanting"
}
```
```json5
{
  "type": "arcane:block_state",
  "providers": [
    {"states": [{"slot_0_occupied": "true"}],"amount": 0.25},
    {"states": [{"slot_1_occupied": "true"}],"amount": 0.25},
    {"states": [{"slot_2_occupied": "true"}],"amount": 0.25},
    {"states": [{"slot_3_occupied": "true"}],"amount": 0.25},
    {"states": [{"slot_4_occupied": "true"}],"amount": 0.25},
    {"states": [{"slot_5_occupied": "true"}],"amount": 0.25}
  ],
  "provider_blocks": "#arcane_test:chiseled_bookshelves",
  "additive": true
}
```
## For Mod Developers:
Besides the obvious ability to use this mod as a "library" of sorts to support custom enchanting power providers, it is also possible to add new enchanting power provider types to this mod.  Simply register them to the `arcane:enchantment_power_provider_types` registry, and they will be usable in the `type` parameter in enchanting power provider files.  I recommend looking to this mod's main class and the class `EnchantmentPowerProviderTypes` for more details on registering custom enchantment power provider types.