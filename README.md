Tweaks
==============

Tweaks - Specific tweaks to the game intended for BitLimit PS* / only attainable through code.

###Info

Created by [Kolin Krewinkel](http://kolinkrewinkel.com/), an iOS developer from California.  
Developed for the [BitLimit](http://maps.bitlimit.com/) community, a top-notch [Minecraft](http://minecraft.net/) community created by [Coestar](http://youtube.com/Coestar). 

###Features

* Sunny periods last for twice as long, rain maintains normal duration (weather).
* TNT can only be placed by owners of all WorldGuard regions at a location 
* Slime spawning is now reduced, randomly preventing slime spawns to reduce annoyances.
* All tweaks are live togglable by either permissioned players or the console, without a reload.
* Error catching and intuitive command guidance when running the /tweaks * family.


###Commands (Player/Console)
* **/tweaks**  
Returns list of potential commands.

* **/tweaks (TNT/tnt|slimes|weather)**
Returns the current state of the passed tweak.  Defined in config.yml under enabled-*.

* **/tweaks (TNT/tnt|slimes|weather) state**  
Accepts any case of yes/no, and any form of *able and its past participle.

###Permissions
*Permission node: tweaks.\*, defaults to op*.  
All noticeably alter game-play, and thus are recommended to be handled under the same permission node as I've done.

###License
Tweaks is licensed under the MIT License.