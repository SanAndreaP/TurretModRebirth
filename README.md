# ![Turret Mod Rebirth][logo]

[![License][b_license]](https://github.com/SanAndreaP/TurretModRebirth/blob/main/LICENSE.md)
[![Version][b_version]](https://github.com/SanAndreaP/TurretModRebirth/tags)
[![Game][b_game]](https://minecraft.net)
[![API][b_api]](https://files.minecraftforge.net)

[![Twitter][b_twitter]](https://twitter.com/SanAndreaP)
[![Reddit][b_reddit]](https://reddit.com/u/SanAndreasP)
[![Sponsor][b_sponsor]](https://github.com/sponsors/SanAndreaP)

## Player Info

**Get the mod on CurseForge: [CurseForge project page](https://www.curseforge.com/minecraft/mc-mods/turret-mod-rebirth)**

<br>

This Minecraft mod adds defense turrets that can protect you and your base from enemies.

To start, you need to craft the **Turret Assembly Table**, in which you can create anything related to this mod.<br>
Recipes not only require items for the craft, but also consume power. Either you already have a compatible power system you can hook the table up to (*Forge Energy* and systems related to that are supported), or you can craft the **Electrolyte Generator**, which you can place right next to it and insert organic matter (potatoes, apples, etc.).

For more information on this mod, you can take a look into the in-game documentation (the "Turret Lexicon") - if you have [Patchouli](https://www.curseforge.com/minecraft/mc-mods/patchouli) installed as well - or read the wiki (comin soon™).

### Demo video

coming soon™

[//]: # ([![DeMossifier Video]&#40;https://github.com/SanAndreaP/DeMossifier/blob/main/.github/DeMossifierVideo.png?raw=true&#41;]&#40;http://www.youtube.com/watch?feature=player_embedded&v=TGOZymE8CBg&#41;)

## Contributor Info

### For Translators

You want to translate the mod to a language you're proficient in? There are two ways of doing so:

1. Fork[^2] this repo and go to the `src/main/resources/assets/sapturretmod/lang` directory. Here you can find the already translated files. Either modify an existing one or create a new file[^1].
   After you've done your changes, make a pull request (PR)[^3].

2. Copy the currently available english translation file ([here](https://raw.githubusercontent.com/SanAndreaP/TurretModRebirth/src/main/resources/assets/sapturretmod/lang/en_us.json)) and create a new .json file[^1] somewhere (or download the file by right-clicking the link and choose `Save Target/Link As...`, optionally rename it).
   After you've done your changes, create a new [issue](https://github.com/SanAndreaP/TurretModRebirth/issues) and attach your file there.

### For Developers

To contribute to this project via code, you will need
- to know how to code with Java,
- to know how to mod with Forge[^4],
- an IDE of your choosing that can handle Java/Gradle projects. I recommend Eclipse or JetBrains IntelliJ IDEA.

Fork[^2] this repo to a directory of your choosing.
If done correctly, you can import the repository as a gradle project with your IDE.
Now make your changes and make a pull request (PR)[^3]. I will review your PR and merge it, if there's no issues.


[^1]: The file name needs to be `[language_code]_[country_code].json` (e.g. `en_us.hjson` for US english). Valid codes can be found here: https://www.fincher.org/Utilities/CountryLanguageList.shtml
[^2]: How to fork a repo: https://docs.github.com/en/desktop/contributing-and-collaborating-using-github-desktop/adding-and-cloning-repositories/cloning-and-forking-repositories-from-github-desktop
[^3]: How to create a pull request: https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request-from-a-fork
[^4]: a beginners guide can be found here: https://moddingtutorials.org/


[logo]: https://raw.githubusercontent.com/SanAndreaP/TurretModRebirth/main/web/logo.png "Turret Mod Rebirth"

[b_license]: https://img.shields.io/github/license/SanAndreaP/TurretModRebirth?style=flat&color=maroon&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iaXNvLTg4NTktMSI%2FPg0KPHN2ZyB2ZXJzaW9uPSIxLjEiIGlkPSJMYXllcl8xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB4PSIwcHgiIHk9IjBweCINCgkgdmlld0JveD0iMCAwIDM5Mi41NTIgMzkyLjU1MiIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgMzkyLjU1MiAzOTIuNTUyOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI%2BDQoJCTxwYXRoIGZpbGw9Im1hcm9vbiIgZD0iTTM5MS40MDMsMjM0LjQyN2wtNTguNjM0LTExNy4yMDRoMzQuMzkyYzUuNDMsMCwxMC40MDgtMy42ODUsMTEuMzc4LTkuMDVjMS4wOTktNi43ODgtNC4wNzMtMTIuNzM1LTEwLjczMS0xMi43MzUNCgkJCWgtODUuMjY5Yy0xNy4wNjcsMC0zMy41NTEtNC4zOTYtNDguMjkxLTEyLjY3MWMxLjY4MS00LjQ2MSwyLjY1MS05LjI0NCwyLjY1MS0xNC4yODdjMC0xOC42MTgtMTIuNjA2LTM0LjMyNy0yOS42NzMtMzkuMTExDQoJCQlWMTEuNTI2YzAtNS40My0zLjY4NS0xMC40MDgtOS4wNTEtMTEuMzc4Yy02Ljc4OC0xLjA5OS0xMi43MzUsNC4wNzMtMTIuNzM1LDEwLjczMXYxOC40ODkNCgkJCWMtMTcuMDY3LDQuNzg0LTI5LjczNywyMC40OTMtMjkuNzM3LDM5LjExMWMwLDUuMDQyLDAuOTA1LDkuODI2LDIuNjUxLDE0LjI4N2MtMTQuNzM5LDguMjc1LTMxLjIyNCwxMi42NzEtNDguMjkxLDEyLjY3MUgyNS41MDQNCgkJCWMtNS40MywwLTEwLjQwOCwzLjY4NS0xMS4zNzgsOS4wNWMtMS4wOTksNi43ODgsNC4wNzMsMTIuNzM1LDEwLjczMSwxMi43MzVoMzQuOTA5TDEuMTk3LDIzNC40MjcNCgkJCWMtMC44NCwxLjY4MS0xLjIyOCwzLjU1Ni0xLjE2NCw1LjQ5NWMwLjMyMyw0Mi40MDgsMzQuOTA5LDc2Ljg2NSw3Ny40NDYsNzYuODY1czc3LjEyMy0zNC40NTcsNzcuNDQ2LTc2Ljg2NQ0KCQkJYzAuMTI5LTEuNzQ1LTAuMjU5LTMuNjItMS4xNjQtNS40OTVMOTUuMTI4LDExNy4yMjNoMTQuOTMzYzIxLjcyMSwwLDQyLjczMS01LjgxOCw2MS4zNS0xNi43NDMNCgkJCWM0LjA3MywzLjIzMiw4Ljg1Nyw1LjYyNCwxNC4wOTMsNy4xMTFWMzI1LjMyYy0yOC41MDksNC4yNjctNTIuNTU4LDI0Ljk1NC02MC40NDQsNTMuMzk4DQoJCQljLTEuOTM5LDYuOTgyLDMuMjk3LDEzLjgzNCwxMC41MzcsMTMuODM0aDEyMS43MjljNy4xNzYsMCwxMi40MTItNi44NTMsMTAuNTM3LTEzLjgzNGMtNy44ODctMjguMzgtMzEuOTM1LTQ5LjA2Ny02MC40NDQtNTMuMzk4DQoJCQlWMTA3LjQ2MWM1LjE3Mi0xLjQ4Nyw5Ljg5MS0zLjg3OSwxMy45NjQtNy4xMTFjMTguNTU0LDEwLjk5LDM5LjU2NCwxNi43NDMsNjEuMzQ5LDE2Ljc0M2gxNC45MzNsLTU4Ljg5MywxMTcuMzMzDQoJCQljLTAuODQsMS41NTItMS4yMjgsMy40MjYtMS4xNjQsNS40OTVjMC4zMjMsNDIuNDA4LDM0LjkwOSw3Ni44NjUsNzcuNDQ2LDc2Ljg2NXM3Ny4xMjMtMzQuMzkyLDc3LjQ0Ni03Ni44NjUNCgkJCUMzOTIuNjMxLDIzNy45MTgsMzkyLjI0MywyMzYuMTA3LDM5MS40MDMsMjM0LjQyN3ogTTc3LjU0NCwyOTQuOTM2Yy0yNi45NTgsMC00OS41MTktMTkuMjY1LTU0LjYyNi00NC43MzVoMTA5LjE4OA0KCQkJQzEyNi45OTksMjc1LjczNiwxMDQuNTAyLDI5NC45MzYsNzcuNTQ0LDI5NC45MzZ6IE0xMjYuNDE3LDIyOC40MTRoLTk3LjgxbDQ4LjkzNy05Ny43NDVMMTI2LjQxNywyMjguNDE0eiBNMjQwLjUxOCwzNzAuNzY2DQoJCQloLTg4LjQzNmM5LjQzOC0xNC45MzMsMjUuOTg4LTI0LjQzNiw0NC4yMTgtMjQuNDM2UzIzMS4wNzksMzU1LjgzMywyNDAuNTE4LDM3MC43NjZ6IE0xOTYuMyw4Ny4yMjcNCgkJCWMtMTAuMzQzLDAtMTguNzQ3LTguNDA0LTE4Ljc0Ny0xOC43NDdjMC0xMC4zNDMsOC40MDQtMTguNzQ3LDE4Ljc0Ny0xOC43NDdoMC4wNjVjMCwwLDAsMCwwLjA2NSwwDQoJCQljMTAuMzQzLDAsMTguNjgzLDguNDA0LDE4LjY4MywxOC43NDdDMjE1LjA0Nyw3OC44MjMsMjA2LjY0Myw4Ny4yMjcsMTk2LjMsODcuMjI3eiBNMzE1LjA1NSwxMzAuNjY5bDQ4LjkzNyw5Ny43NDVoLTk3LjgxDQoJCQlMMzE1LjA1NSwxMzAuNjY5eiBNMzE1LjA1NSwyOTQuOTM2Yy0yNi45NTgsMC00OS41MTktMTkuMjY1LTU0LjU2Mi00NC43MzVoMTA5LjE4OA0KCQkJQzM2NC41NzQsMjc1LjczNiwzNDIuMDEzLDI5NC45MzYsMzE1LjA1NSwyOTQuOTM2eiIvPg0KPC9zdmc%2BDQo%3D "BSD-3-Clause license"

[b_version]: https://img.shields.io/github/v/tag/SanAndreaP/TurretModRebirth?style=flat&color=goldenrod&label=version&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iaXNvLTg4NTktMSI%2FPg0KPHN2ZyB2ZXJzaW9uPSIxLjEiIGlkPSJMYXllcl8xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB4PSIwcHgiIHk9IjBweCINCgkgdmlld0JveD0iMCAwIDM1MC45NTMgMzUwLjk1MyIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgMzUwLjk1MyAzNTAuOTUzOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI%2BDQoJCTxwYXRoIGZpbGw9ImdvbGRlbnJvZCIgZD0iTTI3Ni4xOTksMzQyLjk3N2MtMS45ODgsMC0zLjk4NS0wLjQ3NS01LjgxNC0xLjQzNmwtOTQuOTA4LTQ5Ljg5NmwtOTQuOTA4LDQ5Ljg5NmMtNC4yMTIsMi4yMTMtOS4zMTUsMS44NDUtMTMuMTY0LTAuOTUyDQoJCQljLTMuODUtMi43OTYtNS43NzctNy41MzYtNC45NzMtMTIuMjI3bDE4LjEyNS0xMDUuNjgyTDMuNzc1LDE0Ny44MzZjLTMuNDA3LTMuMzIxLTQuNjMzLTguMjg5LTMuMTYzLTEyLjgxMw0KCQkJYzEuNDctNC41MjUsNS4zODItNy44MjMsMTAuMDkxLTguNTA4bDEwNi4xMTEtMTUuNDE4bDQ3LjQ1NC05Ni4xNTNjMi4xMDUtNC4yNjcsNi40NTEtNi45NjgsMTEuMjA5LTYuOTY4DQoJCQljNC43NTcsMCw5LjEwNCwyLjcwMSwxMS4yMDgsNi45NjhsNDcuNDU0LDk2LjE1M2wxMDYuMTEsMTUuNDE4YzQuNzA5LDAuNjg1LDguNjIsMy45ODIsMTAuMDkxLDguNTA4DQoJCQljMS40NzEsNC41MjQsMC4yNDUsOS40OTItMy4xNjMsMTIuODEzbC03Ni43ODMsNzQuODQ1bDE4LjEyNiwxMDUuNjgyYzAuODA0LDQuNjg5LTEuMTI0LDkuNDMxLTQuOTczLDEyLjIyNw0KCQkJQzI4MS4zNywzNDIuMTcyLDI3OC43OTIsMzQyLjk3NywyNzYuMTk5LDM0Mi45Nzd6IE0zOS4zNjIsMTQ3LjYxNWw2My4zNTIsNjEuNzUyYzIuOTQ2LDIuODcxLDQuMjkxLDcuMDEsMy41OTQsMTEuMDYzDQoJCQlsLTE0Ljk1NSw4Ny4xOTZsNzguMzA2LTQxLjE3YzMuNjQyLTEuOTE0LDcuOTkyLTEuOTE0LDExLjYzNCwwbDc4LjMwNiw0MS4xNjhsLTE0Ljk1Ni04Ny4xOTQNCgkJCWMtMC42OTUtNC4wNTYsMC42NDktOC4xOTIsMy41OTUtMTEuMDYzbDYzLjM1My02MS43NTJsLTg3LjU0OS0xMi43MjJjLTQuMDcxLTAuNTkyLTcuNTkxLTMuMTQ4LTkuNDEyLTYuODM4bC0zOS4xNTItNzkuMzM0DQoJCQlsLTM5LjE1Myw3OS4zMzRjLTEuODIsMy42ODktNS4zNDEsNi4yNDYtOS40MTIsNi44MzhMMzkuMzYyLDE0Ny42MTV6Ii8%2BDQo8L3N2Zz4NCg%3D%3D "Version"

[b_game]: https://img.shields.io/badge/game-Minecraft-limegreen?style=flat&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iaXNvLTg4NTktMSI%2FPg0KPHN2ZyB2ZXJzaW9uPSIxLjEiIGlkPSJMYXllcl8xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB4PSIwcHgiIHk9IjBweCINCgkgdmlld0JveD0iMCAwIDUxMi4wMDMgNTEyLjAwMyIgc3R5bGU9ImVuYWJsZS1iYWNrZ3JvdW5kOm5ldyAwIDAgNTEyLjAwMyA1MTIuMDAzOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI%2BDQoJPGc%2BDQoJCTxwYXRoIGZpbGw9ImxpbWVncmVlbiIgZD0iTTM3Ny40MzEsMjEzLjM0NEgyNzcuMzQ0Vjg1LjMzM2MwLTExLjY4NSw5LjY0OS0yMS4zMzMsMjEuMzMzLTIxLjMzM2MxMS42ODUsMCwyMS4zMzMsOS42NDksMjEuMzMzLDIxLjMzM3YyMS4zMzMNCgkJCWMwLDM1LjI0OSwyOC43NTEsNjQsNjQsNjRjMzUuMjQ5LDAsNjQtMjguNzUxLDY0LTY0VjIxLjMzM0M0NDguMDEsOS41NTEsNDM4LjQ1OSwwLDQyNi42NzcsMHMtMjEuMzMzLDkuNTUxLTIxLjMzMywyMS4zMzMNCgkJCXY4NS4zMzNjMCwxMS42ODUtOS42NDksMjEuMzMzLTIxLjMzMywyMS4zMzNjLTExLjY4NSwwLTIxLjMzMy05LjY0OS0yMS4zMzMtMjEuMzMzVjg1LjMzM2MwLTM1LjI0OS0yOC43NTEtNjQtNjQtNjQNCgkJCWMtMzUuMjQ5LDAtNjQsMjguNzUxLTY0LDY0djEyOC4wMTFoLTk4LjI5MWMtNzUuNTk3LDAtMTM2LjM4NCw2Ny42MTEtMTM2LjM4NCwxNDkuMzEyYzAsNzcuNjQyLDUyLjIzMSwxNDguMzYsMTE5LjY1LDE0OS4zMzENCgkJCWMyMS45MjcsMC4zMTcsNTMuNjIzLTQuMDg3LDgzLjIxOS0xMy4xMDZjMzMuMTgyLTEwLjEyOCw3NS4wMDEtMTAuMTM5LDEwOC4wNDEtMC4wMWMyOC40MjQsOC42OTMsNjQuMDU5LDEzLjQ1OSw4My4zMjcsMTMuMTE1DQoJCQljNjcuMjQ2LTEuMjc1LDExNy43NjItNzEuMzc0LDExNy43NjItMTQ5LjMzQzUxMi4wMDIsMjgwLjY1NCw0NTIuODAyLDIxMy4zNDQsMzc3LjQzMSwyMTMuMzQ0eiBNMzkzLjQ1Myw0NjkuMzI3DQoJCQljLTE0Ljg1MSwwLjI2Ni00Ni4wMi0zLjkwMy03MC4wNS0xMS4yNTJjLTQxLjE3Mi0xMi42MjEtOTEuNjgzLTEyLjYwOC0xMzIuOTc5LTAuMDAzYy0yNS4zMjEsNy43MTYtNTIuNjEzLDExLjUwOC03MC4xNTcsMTEuMjU0DQoJCQljLTQxLjAwNS0wLjU5MS03Ny41OTktNTAuMTM3LTc3LjU5OS0xMDYuNjY5YzAtNTkuMDg5LDQyLjc1Ni0xMDYuNjQ1LDkzLjcxNy0xMDYuNjQ1aDI0MS4wNDUNCgkJCWM1MC41LDAsOTEuOTA0LDQ3LjA3Niw5MS45MDQsMTA2LjY0NUM0NjkuMzM1LDQxOS42NzgsNDM0LjExMiw0NjguNTU2LDM5My40NTMsNDY5LjMyN3oiLz4NCgkJPHBhdGggZmlsbD0ibGltZWdyZWVuIiBkPSJNMzYyLjY3NywzNDEuMzMzYzExLjc3NiwwLDIxLjMzMy05LjU1NywyMS4zMzMtMjEuMzMzcy05LjU1Ny0yMS4zMzMtMjEuMzMzLTIxLjMzM3MtMjEuMzMzLDkuNTU3LTIxLjMzMywyMS4zMzMNCgkJCVMzNTAuOTAxLDM0MS4zMzMsMzYyLjY3NywzNDEuMzMzeiIvPg0KCQk8cGF0aCBmaWxsPSJsaW1lZ3JlZW4iIGQ9Ik0zNjIuNjc3LDM4NGMtMTEuNzc2LDAtMjEuMzMzLDkuNTU3LTIxLjMzMywyMS4zMzNzOS41NTcsMjEuMzMzLDIxLjMzMywyMS4zMzNzMjEuMzMzLTkuNTU3LDIxLjMzMy0yMS4zMzMNCgkJCVMzNzQuNDUzLDM4NCwzNjIuNjc3LDM4NHoiLz4NCgkJPHBhdGggZmlsbD0ibGltZWdyZWVuIiBkPSJNMzIwLjAxLDM0MS4zMzNjLTExLjc3NiwwLTIxLjMzMyw5LjU1Ny0yMS4zMzMsMjEuMzMzUzMwOC4yMzQsMzg0LDMyMC4wMSwzODRzMjEuMzMzLTkuNTU3LDIxLjMzMy0yMS4zMzMNCgkJCVMzMzEuNzg2LDM0MS4zMzMsMzIwLjAxLDM0MS4zMzN6Ii8%2BDQoJCTxwYXRoIGZpbGw9ImxpbWVncmVlbiIgZD0iTTQwNS4zNDQsMzQxLjMzM2MtMTEuNzc2LDAtMjEuMzMzLDkuNTU3LTIxLjMzMywyMS4zMzNTMzkzLjU2OCwzODQsNDA1LjM0NCwzODRzMjEuMzMzLTkuNTU3LDIxLjMzMy0yMS4zMzMNCgkJCVM0MTcuMTIsMzQxLjMzMyw0MDUuMzQ0LDM0MS4zMzN6Ii8%2BDQoJCTxwYXRoIGZpbGw9ImxpbWVncmVlbiIgZD0iTTE5Mi4wMSwzNDEuMzMzaC0yMS4zMzNWMzIwYzAtMTEuNzgyLTkuNTUxLTIxLjMzMy0yMS4zMzMtMjEuMzMzYy0xMS43ODIsMC0yMS4zMzMsOS41NTEtMjEuMzMzLDIxLjMzM3YyMS4zMzNoLTIxLjMzMw0KCQkJYy0xMS43ODIsMC0yMS4zMzMsOS41NTEtMjEuMzMzLDIxLjMzM1M5NC44OTUsMzg0LDEwNi42NzcsMzg0aDIxLjMzM3YyMS4zMzNjMCwxMS43ODIsOS41NTEsMjEuMzMzLDIxLjMzMywyMS4zMzMNCgkJCWMxMS43ODIsMCwyMS4zMzMtOS41NTEsMjEuMzMzLTIxLjMzM1YzODRoMjEuMzMzYzExLjc4MiwwLDIxLjMzMy05LjU1MSwyMS4zMzMtMjEuMzMzUzIwMy43OTIsMzQxLjMzMywxOTIuMDEsMzQxLjMzM3oiLz4NCgk8L2c%2BDQo8L3N2Zz4NCg%3D%3D "Minecraft"

[b_api]: https://img.shields.io/badge/mod%20API-Forge-gainsboro?style=flat&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiA%2FPg0KPHN2ZyB3aWR0aD0iMzJweCIgaGVpZ2h0PSIzMnB4IiB2aWV3Qm94PSIwIDAgMzIgMzIiIGlkPSJPdXRsaW5lZCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4NCgk8dGl0bGUvPg0KCTxnIGlkPSJGaWxsIj4NCgkJPHBhdGggZmlsbD0iZ2FpbnNib3JvIiBkPSJNMTYsMTJhNCw0LDAsMSwwLDQsNEE0LDQsMCwwLDAsMTYsMTJabTAsNmEyLDIsMCwxLDEsMi0yQTIsMiwwLDAsMSwxNiwxOFoiLz4NCgkJPHBhdGggZmlsbD0iZ2FpbnNib3JvIiBkPSJNMjcuNTcsMTguM2wtMS4zOC0uNzlhOS41Niw5LjU2LDAsMCwwLDAtM2wxLjM4LS43OWEyLjg2LDIuODYsMCwwLDAsMS4wNS0zLjkxbC0uOTMtMS42MWEyLjg4LDIuODgsMCwwLDAtMy45MS0xbC0xLjM4LjhhMTAuMDcsMTAuMDcsMCwwLDAtMi42MS0xLjUyVjQuODNBMi44NiwyLjg2LDAsMCwwLDE2LjkzLDJIMTUuMDdhMi44NiwyLjg2LDAsMCwwLTIuODYsMi44NlY2LjQxQTEwLjA3LDEwLjA3LDAsMCwwLDkuNiw3LjkzbC0xLjM4LS44YTIuODYsMi44NiwwLDAsMC0zLjkxLDFMMy4zOCw5Ljc5YTIuODgsMi44OCwwLDAsMCwxLDMuOTFsMS4zOC43OWE5LjU2LDkuNTYsMCwwLDAsMCwzbC0xLjM4Ljc5YTIuODYsMi44NiwwLDAsMC0xLDMuOTFsLjkzLDEuNjFhMi44OCwyLjg4LDAsMCwwLDMuOTEsMS4wNWwxLjM4LS44YTEwLjA3LDEwLjA3LDAsMCwwLDIuNjEsMS41MnYxLjU4QTIuODYsMi44NiwwLDAsMCwxNS4wNywzMGgxLjg2YTIuODYsMi44NiwwLDAsMCwyLjg2LTIuODZWMjUuNTlhMTAuMDcsMTAuMDcsMCwwLDAsMi42MS0xLjUybDEuMzguOGEyLjg2LDIuODYsMCwwLDAsMy45MS0xLjA1bC45My0xLjYxQTIuODgsMi44OCwwLDAsMCwyNy41NywxOC4zWm0tLjY5LDIuOTFMMjYsMjIuODJhLjg2Ljg2LDAsMCwxLTEuMTcuMzJsLTIuNjMtMS41Mi0uNTQuNDlhOC4zLDguMywwLDAsMS0zLjEyLDEuODJsLS43LjIydjNhLjg2Ljg2LDAsMCwxLS44Ni44NkgxNS4wN2EuODYuODYsMCwwLDEtLjg2LS44NnYtM2wtLjctLjIyYTguMyw4LjMsMCwwLDEtMy4xMi0xLjgybC0uNTQtLjQ5TDcuMjIsMjMuMTRhLjg3Ljg3LDAsMCwxLS42NS4wOC44NC44NCwwLDAsMS0uNTItLjRsLS45My0xLjYxQS44Ni44NiwwLDAsMSw1LjQzLDIwbDIuNjItMS41MS0uMTYtLjcyYTguNTYsOC41NiwwLDAsMS0uMi0xLjgsOCw4LDAsMCwxLC4yMS0xLjhsLjE1LS43Mkw1LjQzLDEyYS44Ni44NiwwLDAsMS0uMzEtMS4xOGwuOTMtMS42MWEuODYuODYsMCwwLDEsMS4xNy0uMzJsMi42MywxLjUyLjU0LS40OWE4LjMsOC4zLDAsMCwxLDMuMTItMS44MmwuNy0uMjJ2LTNBLjg2Ljg2LDAsMCwxLDE1LjA3LDRoMS44NmEuODYuODYsMCwwLDEsLjg2Ljg2djNsLjcuMjJhOC4zLDguMywwLDAsMSwzLjEyLDEuODJsLjU0LjQ5LDIuNjMtMS41MmEuODcuODcsMCwwLDEsLjY1LS4wOC44NC44NCwwLDAsMSwuNTIuNGwuOTMsMS42MUEuODYuODYsMCwwLDEsMjYuNTcsMTJMMjQsMTMuNDhsLjE2LjcyYTguNTYsOC41NiwwLDAsMSwuMiwxLjgsOCw4LDAsMCwxLS4yMSwxLjhsLS4xNS43MkwyNi41NywyMEEuODYuODYsMCwwLDEsMjYuODgsMjEuMjFaIi8%2BDQoJPC9nPg0KPC9zdmc%2B "Forge"

[b_twitter]: https://img.shields.io/badge/twitter-%40SanAndreaP-deepskyblue?style=flat&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iaXNvLTg4NTktMSI%2FPg0KPHN2ZyB3aWR0aD0iMjRweCIgaGVpZ2h0PSIyNHB4IiB2aWV3Qm94PSIwIDAgMjQgMjQiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgZGF0YS1uYW1lPSJMYXllciAxIj4NCgk8cGF0aCBmaWxsPSJkZWVwc2t5Ymx1ZSIgZD0iTTIyLjk5MTIxLDMuOTUwMmEuOTk5NDIuOTk5NDIsMCwwLDAtMS41MTA3NC0uODU5MzgsNy40Nzk1Niw3LjQ3OTU2LDAsMCwxLTEuODczLjc5Myw1LjE1MjM0LDUuMTUyMzQsMCwwLDAtMy4zNzQtMS4yNDIxOSw1LjIzMjM4LDUuMjMyMzgsMCwwLDAtNS4yMjM2Myw1LjA2MzQ4QTExLjAzMTk0LDExLjAzMTk0LDAsMCwxLDQuMTk2MjksMy43ODEyNSwxLjAxMTU0LDEuMDExNTQsMCwwLDAsMy4zMzg4NywzLjQxNmEuOTk4NTIuOTk4NTIsMCwwLDAtLjc4NTE2LjUsNS4yNzU1LDUuMjc1NSwwLDAsMC0uMjQyMTksNC43Njg1NWwtLjAwMTk1LjAwMTk1YTEuMDQxMSwxLjA0MTEsMCwwLDAtLjQ5NTEyLjg4ODY4LDMuMDQxNzQsMy4wNDE3NCwwLDAsMCwuMDI2MzcuNDM5NDUsNS4xODU0LDUuMTg1NCwwLDAsMCwxLjU2ODM2LDMuMzEyNS45OTgxMy45OTgxMywwLDAsMC0uMDY2NDEuNzY5NTMsNS4yMDQzNiw1LjIwNDM2LDAsMCwwLDIuMzYyMzEsMi45MjE4Nyw3LjQ2NDY0LDcuNDY0NjQsMCwwLDEtMy41ODk4NS40NDgyNS45OTk3NS45OTk3NSwwLDAsMC0uNjY1LDEuODMzQTEyLjk0MjQ4LDEyLjk0MjQ4LDAsMCwwLDguNDYsMjEuMzYxMzMsMTIuNzg3OCwxMi43ODc4LDAsMCwwLDIwLjkyNDgsMTEuOTk4LDEyLjgyMTY2LDEyLjgyMTY2LDAsMCwwLDIxLjQ2LDguMzUxNTZjMC0uMDY1NDMsMC0uMTMyODEtLjAwMS0uMjAwMTlBNS43Njk2Myw1Ljc2OTYzLDAsMCwwLDIyLjk5MTIxLDMuOTUwMlpNMTkuNjg0NTcsNy4xNjIxMWEuOTk1Ljk5NSwwLDAsMC0uMjMzNC43MDIxNWMuMDA5NzcuMTY1LjAwODc5LjMzMS4wMDg3OS40ODczYTEwLjgyMzcxLDEwLjgyMzcxLDAsMCwxLS40NTQxLDMuMDgxMDZBMTAuNjg0NTcsMTAuNjg0NTcsMCwwLDEsOC40NiwxOS4zNjEzM2ExMC45Mzc5MSwxMC45Mzc5MSwwLDAsMS0yLjU1MDc4LS4zMDA3OCw5LjQ3OTUxLDkuNDc5NTEsMCwwLDAsMi45NDIzOC0xLjU2MzQ4QTEuMDAwMzMsMS4wMDAzMywwLDAsMCw4LjI1LDE1LjcxMDk0YTMuMjA4LDMuMjA4LDAsMCwxLTIuMjEzODctLjkzNDU3cS4yMjQxMy0uMDQyNDguNDQ1MzItLjEwNTQ3YTEuMDAwMjYsMS4wMDAyNiwwLDAsMC0uMDgwMDgtMS45NDMzNiwzLjE5ODI0LDMuMTk4MjQsMCwwLDEtMi4yNS0xLjcyNTU5LDUuMjk5MjksNS4yOTkyOSwwLDAsMCwuNTQ0OTIuMDQ1OSwxLjAyMDkzLDEuMDIwOTMsMCwwLDAsLjk4MzQtLjY5NjI5QS45OTk4Ljk5OTgsMCwwLDAsNS4yNzkzLDkuMjE0ODQsMy4xOTU1OSwzLjE5NTU5LDAsMCwxLDMuODU1NDcsNi41NDJjMC0uMDY2NC4wMDE5NS0uMTMyODEuMDA1ODYtLjE5ODI0YTEzLjAxMzY1LDEzLjAxMzY1LDAsMCwwLDguMjA5LDMuNDc5NDksMS4wMjA0NiwxLjAyMDQ2LDAsMCwwLC44MTczOS0uMzU4NCwxLjAwMDM3LDEuMDAwMzcsMCwwLDAsLjIwNi0uODY4MTYsMy4xNTY1MywzLjE1NjUzLDAsMCwxLS4wODY5MS0uNzI4NTJBMy4yMywzLjIzLDAsMCwxLDE2LjIzMzQsNC42NDE2YTMuMTg0MjgsMy4xODQyOCwwLDAsMSwyLjM0NDcyLDEuMDIwNTFBLjk5My45OTMsMCwwLDAsMTkuNDk5LDUuOTZhOS4yNzA3Myw5LjI3MDczLDAsMCwwLDEuMjExOTItLjMyMjI2QTYuNjgxMjYsNi42ODEyNiwwLDAsMSwxOS42ODQ1Nyw3LjE2MjExWiIvPg0KPC9zdmc%2B "Twitter @SanAndreaP"

[b_reddit]: https://img.shields.io/badge/reddit-%2Fu%2FSanAndreasP-orangered?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB2aWV3Qm94PSIwIDAgMzIgMzIiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI%2BDQoJPHBhdGggZmlsbD0ib3JhbmdlcmVkIiBkPSJNIDE4LjY1NjI1IDQgQyAxNi41NTg1OTQgNCAxNSA1LjcwNzAzMSAxNSA3LjY1NjI1IEwgMTUgMTEuMDMxMjUgQyAxMi4yNDIxODggMTEuMTc1NzgxIDkuNzQyMTg4IDExLjkwNjI1IDcuNzE4NzUgMTMuMDYyNSBDIDYuOTQ1MzEzIDEyLjMxNjQwNiA1LjkxNDA2MyAxMiA0LjkwNjI1IDEyIEMgMy44MTY0MDYgMTIgMi43MDcwMzEgMTIuMzU1NDY5IDEuOTM3NSAxMy4yMTg3NSBMIDEuOTM3NSAxMy4yNSBMIDEuOTA2MjUgMTMuMjgxMjUgQyAxLjE2Nzk2OSAxNC4yMDMxMjUgMC44NjcxODggMTUuNDMzNTk0IDEuMDYyNSAxNi42NTYyNSBDIDEuMjQyMTg4IDE3Ljc3NzM0NCAxLjg5ODQzOCAxOC45MTc5NjkgMy4wMzEyNSAxOS42NTYyNSBDIDMuMDIzNDM4IDE5Ljc2OTUzMSAzIDE5Ljg4MjgxMyAzIDIwIEMgMyAyMi42MDU0NjkgNC41NzQyMTkgMjQuODg2NzE5IDYuOTM3NSAyNi40Njg3NSBDIDkuMzAwNzgxIDI4LjA1MDc4MSAxMi40ODgyODEgMjkgMTYgMjkgQyAxOS41MTE3MTkgMjkgMjIuNjk5MjE5IDI4LjA1MDc4MSAyNS4wNjI1IDI2LjQ2ODc1IEMgMjcuNDI1NzgxIDI0Ljg4NjcxOSAyOSAyMi42MDU0NjkgMjkgMjAgQyAyOSAxOS44ODI4MTMgMjguOTc2NTYzIDE5Ljc2OTUzMSAyOC45Njg3NSAxOS42NTYyNSBDIDMwLjEwMTU2MyAxOC45MTc5NjkgMzAuNzU3ODEzIDE3Ljc3NzM0NCAzMC45Mzc1IDE2LjY1NjI1IEMgMzEuMTMyODEzIDE1LjQzMzU5NCAzMC44MzIwMzEgMTQuMjAzMTI1IDMwLjA5Mzc1IDEzLjI4MTI1IEwgMzAuMDYyNSAxMy4yNSBDIDI5LjI5Mjk2OSAxMi4zODY3MTkgMjguMTgzNTk0IDEyIDI3LjA5Mzc1IDEyIEMgMjYuMDg1OTM4IDEyIDI1LjA1NDY4OCAxMi4zMTY0MDYgMjQuMjgxMjUgMTMuMDYyNSBDIDIyLjI1NzgxMyAxMS45MDYyNSAxOS43NTc4MTMgMTEuMTc1NzgxIDE3IDExLjAzMTI1IEwgMTcgNy42NTYyNSBDIDE3IDYuNjc1NzgxIDE3LjU1ODU5NCA2IDE4LjY1NjI1IDYgQyAxOS4xNzU3ODEgNiAxOS44MjAzMTMgNi4yNDYwOTQgMjAuODEyNSA2LjU5Mzc1IEMgMjEuNjU2MjUgNi44OTA2MjUgMjIuNzUgNy4yMTg3NSAyNC4xNTYyNSA3LjMxMjUgQyAyNC40OTYwOTQgOC4yODkwNjMgMjUuNDE0MDYzIDkgMjYuNSA5IEMgMjcuODc1IDkgMjkgNy44NzUgMjkgNi41IEMgMjkgNS4xMjUgMjcuODc1IDQgMjYuNSA0IEMgMjUuNTU0Njg4IDQgMjQuNzM4MjgxIDQuNTM1MTU2IDI0LjMxMjUgNS4zMTI1IEMgMjMuMTEzMjgxIDUuMjQyMTg4IDIyLjI0NjA5NCA0Ljk5MjE4OCAyMS40Njg3NSA0LjcxODc1IEMgMjAuNTY2NDA2IDQuNDAyMzQ0IDE5LjczNDM3NSA0IDE4LjY1NjI1IDQgWiBNIDE2IDEzIEMgMTkuMTUyMzQ0IDEzIDIxLjk2NDg0NCAxMy44NjcxODggMjMuOTM3NSAxNS4xODc1IEMgMjUuOTEwMTU2IDE2LjUwNzgxMyAyNyAxOC4yMDMxMjUgMjcgMjAgQyAyNyAyMS43OTY4NzUgMjUuOTEwMTU2IDIzLjQ5MjE4OCAyMy45Mzc1IDI0LjgxMjUgQyAyMS45NjQ4NDQgMjYuMTMyODEzIDE5LjE1MjM0NCAyNyAxNiAyNyBDIDEyLjg0NzY1NiAyNyAxMC4wMzUxNTYgMjYuMTMyODEzIDguMDYyNSAyNC44MTI1IEMgNi4wODk4NDQgMjMuNDkyMTg4IDUgMjEuNzk2ODc1IDUgMjAgQyA1IDE4LjIwMzEyNSA2LjA4OTg0NCAxNi41MDc4MTMgOC4wNjI1IDE1LjE4NzUgQyAxMC4wMzUxNTYgMTMuODY3MTg4IDEyLjg0NzY1NiAxMyAxNiAxMyBaIE0gNC45MDYyNSAxNCBDIDUuMjg1MTU2IDE0IDUuNjYwMTU2IDE0LjA5Mzc1IDUuOTY4NzUgMTQuMjUgQyA0Ljg4MjgxMyAxNS4xNjAxNTYgNC4wMzkwNjMgMTYuMjQyMTg4IDMuNTMxMjUgMTcuNDM3NSBDIDMuMjc3MzQ0IDE3LjExNzE4OCAzLjEyNSAxNi43MzQzNzUgMy4wNjI1IDE2LjM0Mzc1IEMgMi45NTMxMjUgMTUuNjcxODc1IDMuMTQ4NDM4IDE0Ljk3NjU2MyAzLjQ2ODc1IDE0LjU2MjUgQyAzLjQ3MjY1NiAxNC41NTQ2ODggMy40NjQ4NDQgMTQuNTM5MDYzIDMuNDY4NzUgMTQuNTMxMjUgQyAzLjc3MzQzOCAxNC4yMTA5MzggNC4zMTI1IDE0IDQuOTA2MjUgMTQgWiBNIDI3LjA5Mzc1IDE0IEMgMjcuNjg3NSAxNCAyOC4yMjY1NjMgMTQuMjEwOTM4IDI4LjUzMTI1IDE0LjUzMTI1IEMgMjguNTM1MTU2IDE0LjUzNTE1NiAyOC41MjczNDQgMTQuNTU4NTk0IDI4LjUzMTI1IDE0LjU2MjUgQyAyOC44NTE1NjMgMTQuOTc2NTYzIDI5LjA0Njg3NSAxNS42NzE4NzUgMjguOTM3NSAxNi4zNDM3NSBDIDI4Ljg3NSAxNi43MzQzNzUgMjguNzIyNjU2IDE3LjExNzE4OCAyOC40Njg3NSAxNy40Mzc1IEMgMjcuOTYwOTM4IDE2LjI0MjE4OCAyNy4xMTcxODggMTUuMTYwMTU2IDI2LjAzMTI1IDE0LjI1IEMgMjYuMzM5ODQ0IDE0LjA5Mzc1IDI2LjcxNDg0NCAxNCAyNy4wOTM3NSAxNCBaIE0gMTEgMTYgQyA5Ljg5NDUzMSAxNiA5IDE2Ljg5NDUzMSA5IDE4IEMgOSAxOS4xMDU0NjkgOS44OTQ1MzEgMjAgMTEgMjAgQyAxMi4xMDU0NjkgMjAgMTMgMTkuMTA1NDY5IDEzIDE4IEMgMTMgMTYuODk0NTMxIDEyLjEwNTQ2OSAxNiAxMSAxNiBaIE0gMjEgMTYgQyAxOS44OTQ1MzEgMTYgMTkgMTYuODk0NTMxIDE5IDE4IEMgMTkgMTkuMTA1NDY5IDE5Ljg5NDUzMSAyMCAyMSAyMCBDIDIyLjEwNTQ2OSAyMCAyMyAxOS4xMDU0NjkgMjMgMTggQyAyMyAxNi44OTQ1MzEgMjIuMTA1NDY5IDE2IDIxIDE2IFogTSAyMS4yNSAyMS41MzEyNSBDIDIwLjEwMTU2MyAyMi41OTc2NTYgMTguMTcxODc1IDIzLjI4MTI1IDE2IDIzLjI4MTI1IEMgMTMuODI4MTI1IDIzLjI4MTI1IDExLjg5ODQzOCAyMi41ODk4NDQgMTAuNzUgMjEuNjU2MjUgQyAxMS4zOTA2MjUgMjMuMzkwNjI1IDEzLjQ0NTMxMyAyNSAxNiAyNSBDIDE4LjU1NDY4OCAyNSAyMC42MDkzNzUgMjMuMzk4NDM4IDIxLjI1IDIxLjUzMTI1IFoiLz4NCjwvc3ZnPg%3D%3D "/u/SanAndreasP"

[b_sponsor]: https://img.shields.io/github/sponsors/SanAndreaP?style=flat&color=pink&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iaXNvLTg4NTktMSI%2FPg0KPHN2ZyB2ZXJzaW9uPSIxLjEiIGlkPSJMYXllcl8xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB4PSIwcHgiIHk9IjBweCINCgkgdmlld0JveD0iMCAwIDI5NyAyOTciIHN0eWxlPSJlbmFibGUtYmFja2dyb3VuZDpuZXcgMCAwIDI5NyAyOTc7IiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCgk8cGF0aCBmaWxsPSJwaW5rIiBkPSJNMTQ4LjUsMjczLjk2Yy0xLjU3MiwwLTMuMTQ1LTAuMzYtNC41ODktMS4wODNjLTEuNDU1LTAuNzI4LTM2LjAyOC0xOC4xNDktNzEuMS00Ny4zNzUNCgkJYy0yMC44MjQtMTcuMzUzLTM3LjQ3NS0zNS4yODktNDkuNDg4LTUzLjMxQzcuODQ3LDE0OC45NzksMCwxMjUuNTA0LDAsMTAyLjQyMUMwLDU4LjY1LDM1LjYxLDIzLjA0LDc5LjM4MSwyMy4wNA0KCQljMjkuNjA0LDAsNTUuNDc0LDE2LjI4Niw2OS4xMTksNDAuMzcyYzEzLjY0NS0yNC4wODYsMzkuNTE2LTQwLjM3Miw2OS4xMTktNDAuMzcyYzQzLjc3LDAsNzkuMzgxLDM1LjYxLDc5LjM4MSw3OS4zODENCgkJYzAsMjMuMDgzLTcuODQ3LDQ2LjU1OC0yMy4zMjMsNjkuNzcxYy0xMi4wMTQsMTguMDIxLTI4LjY2NCwzNS45NTctNDkuNDg4LDUzLjMxMWMtMzUuMDcxLDI5LjIyNi02OS42NDUsNDYuNjQ3LTcxLjEsNDcuMzc0DQoJCUMxNTEuNjQ1LDI3My42LDE1MC4wNzIsMjczLjk2LDE0OC41LDI3My45NnogTTc5LjM4MSw0My41NjRjLTMyLjQ1MywwLTU4Ljg1NiwyNi40MDMtNTguODU2LDU4Ljg1Ng0KCQljMCw3NS43MzEsMTA0LjU4NCwxMzYuOTMxLDEyNy45NzIsMTQ5LjY2NWMyMy4zNzktMTIuNzUsMTI3Ljk3OS03NC4wNDQsMTI3Ljk3OS0xNDkuNjY1YzAtMzIuNDUzLTI2LjQwMy01OC44NTYtNTguODU2LTU4Ljg1Ng0KCQljLTMyLjQ1NCwwLTU4Ljg1NiwyNi40MDMtNTguODU2LDU4Ljg1NmMwLDUuNjY3LTQuNTk2LDEwLjI2My0xMC4yNjMsMTAuMjYzcy0xMC4yNjMtNC41OTYtMTAuMjYzLTEwLjI2Mw0KCQlDMTM4LjIzNyw2OS45NjgsMTExLjgzNSw0My41NjQsNzkuMzgxLDQzLjU2NHoiLz4NCjwvc3ZnPg0K "Sponsor me on GitHub"