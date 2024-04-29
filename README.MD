# CloudAnarchyCore

A singular, integrated, monolithic plugin developed to rectify vulnerabilities and enhance capabilities within anarchic server environments to achieve blockgame supremacy.

---

###### Versions

| git                                                                              | version | sw    | mc     |
|----------------------------------------------------------------------------------|---------|-------|--------|
| [master](https://github.com/zeroBzeroT/CloudAnarchyCore/tree/master)             | 3.4.x   | paper | 1.20.x |
| [1.20.4_folia](https://github.com/zeroBzeroT/CloudAnarchyCore/tree/1.20.4_folia) | 3.3.x   | folia | 1.20.4 |
| [1.20.2_folia](https://github.com/zeroBzeroT/CloudAnarchyCore/tree/1.20.2_folia) | 3.2.x   | folia | 1.20.2 |
| [1.20.1_paper](https://github.com/zeroBzeroT/CloudAnarchyCore/tree/1.20.1_paper) | 3.1.x   | paper | 1.20.1 |
| [1.17.1_paper](https://github.com/zeroBzeroT/CloudAnarchyCore/tree/1.17.1_paper) | 2.x.x   | paper | 1.17.1 |
| [1.12.2_paper](https://github.com/zeroBzeroT/CloudAnarchyCore/tree/1.12.2_paper) | 1.x.x   | paper | 1.12.2 |

---

###### Building

Run gradle task **build** to build a fat jar that includes dependencies.

To run from terminal, use: `./gradlew build` / `gradlew.bat build`

---

###### Testing

Run gradle task **runFolia** to start a local test server.

To run from terminal, use: `./gradlew runFolia` / `gradlew.bat runFolia`

---

###### Misc

to init uninitialized submodules, run:

```
git submodule update --init --recursive
```

to update the repositories git submodules, run:

```
git pull --recurse-submodules                \
&& git submodule update --remote --recursive \
&& git add convenience redis                 \
&& git commit -m "updated submodules"
```

to create and push a new tag, run:

```
git tag 1.2.3
git push origin 1.2.3
```

[module architecture info](module-architecture-readme.md)
