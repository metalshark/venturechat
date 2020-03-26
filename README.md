# README #

Read before making any changes or pull requests!

### What is this repository for? ###

VentureChat Spigot plugin Repo

### How do I get set up? ###

Full setup and dependency guide is available here on SpigotMC: https://www.spigotmc.org/resources/venturechat.771/

### Contribution guidelines ###

Keep changes simple and clear for me to understand

To compile you will need to download the following libraries:

- [craftbukkit-1.8.4-R0.1-SNAPSHOT-latest.jar](https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.8.4-R0.1-SNAPSHOT-latest.jar)
- [Factions.jar](https://www.spigotmc.org/resources/factions.1900/download?version=228044)
- [MassiveCore.jar](https://www.spigotmc.org/resources/factions.1900/download?version=228044)
- [Towny-0.96.0.0.jar](https://github.com/TownyAdvanced/Towny/releases/download/0.96.0.0/Towny.Advanced.0.96.0.0.zip)

Then install them into your local Maven repository using:

```sh
mvn install:install-file -Dfile=craftbukkit-1.8.4-R0.1-SNAPSHOT-latest.jar -DgroupId=org.bukkit -DartifactId=craftbukkit -Dversion=1.8.4-R0.1-SNAPSHOT -Dpackaging=jar
mvn install:install-file -Dfile=Factions.jar -DgroupId=com.github.MassiveCraft -DartifactId=Factions -Dversion=2.14.0 -Dpackaging=jar
mvn install:install-file -Dfile=MassiveCore.jar -DgroupId=com.github.MassiveCraft -DartifactId=MassiveCore -Dversion=2.14.0 -Dpackaging=jar
mvn install:install-file -Dfile=Towny-0.96.0.0.jar -DgroupId=com.palmergames.bukkit.towny -DartifactId=Towny -Dversion=0.96.0.0 -Dpackaging=jar
```

### Who do I talk to? ###

* If you are going to try and make all these changes, you should be talking to me directly otherwise I will more than likely just decline them.

License:

Copyright (C) {2014}  {Austin Brolly}

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.