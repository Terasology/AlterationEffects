# Alteration Effects module
The Alteration Effects module contains a set of status effects (buffs and debuffs) that can alter a player's base state in some fashion.

These are the `AlterationEffects` that current exist in the module:  
- `HealthBoostAlterationEffect`- this increases the maximum health of the player by a specified amount 
- `WaterBreathingAlterationEffect` - this allows the player to breathe in water i.e. they do not take damage from staying in the water for too long
- `DamageOverTimeAlterationEffect` - this deals damage of a specified amount to the player at regular intervals 
- `CureAllDamageOverTimeAlterationEffect` - this removes any damage over time effect on the play immediately
- `CureDamageOverTimeAlterationEffect` - this counteracts damage dealt by any damage over time effect on the player 
- `DecoverAlterationEffect` - this prevents the player from healing from any source
- `RegenerationAlterationEffect` - this heals the entity for a specified amount at regular time intervals
- `ResistDamageAlterationEffect` - this reduces damage dealt of a specific type to the player by a specified amount 
- `GlueAlterationEffect` - this reduces the player's movement speed by 10% and prevents them from jumping
- `ItemUseSpeedAlterationEffect` - this reduces cooldowns on items used by the player for a specified amount
- `JumpSpeedAlterationEffect` - this increases the speed of the player's jump by a specified amount i.e. they jump higher
- `MultiJumpAlterationEffect` - this allows to player to multi-jump in the iar for a specified nuber of times
- `StunAlterationEffect` - this prevents the player from moving or jumping
- `SwimSpeedAlterationEffect` - this increases the speed with which the players swims by a specified amount
- `WalkSpeedAlterationEffect` - this increases the speed with which the player walks by a specified amount

# Usage
All `AlterationEffect` objects have two methods that you can use to apply the effects

`applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration)`  
`applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration)`

`instigator` - the entity who applies the effect  
`entity` - the entity the effect is applied on  
`magnitude` - the magnitude of the effect  
`duration` -  the duration of the effect, in milliseconds  
`id` - an optional ID for certain effects which require them (e.g. ResistDamage)

Not all alteration effects will make use of all these parameters. Take a look at the individual classes to see which parameters are used and how.

# Example
The following example shows how you can initialise a StunAlterationEffect object and apply it on an entity.
```
@In
private Context context

public void applyStunEffect(EntityRef instigator, EntityRef player) {

    StunAlterationEffect stunAlterationEffect = new StunAlterationEffect(context);

    //the instigator applies a stun effect on the player for 10 seconds
    //the magnitude parameter is not used by StunAlterationEffect
    applyEffect(instigator, player, 0, 10000);

}
```