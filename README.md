# artifacts-purger
Extension for Maven to keep your local repository less cluttered.

Since Maven doesn't manage your local repository, its disk space usage can grow wild if left unchecked. In order to help managing the repository's size, a simple "artifacts purger" was created to delete older versions of locally installed Maven projects.

### How to use:
1. Build `artifacts-purger` using Maven (compatible with Java 6).
2. Drop it on `$MAVEN_HOME/lib/ext` directory.
3. Done! Now you just need to build your projects. By default, it will keep only the last 10 installed versions.

**Important:** this extension only activates when executing the goals `install`, `site` or `deploy`.

### Does it work with multi-modular projects?
Yes, it does. :)

### Can I specify how many versions to keep?
Of course. Just define the property `artifactspurger.numVersionsToKeep`. E.g.:
```bash
mvn install -Dartifactspurger.numVersionsToKeep=3
```

### And if there's any problem?
The extension keeps a log, located on `$MAVEN_HOME/log` (created on demand). By default, it logs only `WARNING` or above.
