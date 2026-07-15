<div align="center">
	<h1>PghStatic</h1>
</div>

**PghStatic** is an official plugin which serves the contents of `static/` as an explorable directory tree. Its purpose
is to provide a baseplate for uploading files and seeing the results straight away. Minimal effort, high reward!

As an example, here's the server's directory:

```tree
myserver
├── indexes.txt
├── pghserver-runtime.jar
├── plugins
│   └── pghstatic.jar
└── static
    ├── cat.jpg
    └── index.html
```

## URL to File Mapping

See that `cat.jpg` below? It can be viewed with a URL such as http://localhost:3000/media/cat.jpg.  
That `index.html` below? If it's in your `indexes.txt` (which it is by default), you can visit it
at http://localhost:3000. Otherwise, maybe try http://localhost:3000/index.html?

These rules also apply to subdirectories, which means these all map to URLs:

```tree
myserver
├── indexes.txt               (private!)
├── pghserver-runtime.jar     (private!)
├── plugins                   (private!)
│   ├── dewirecore.jar        (private!)
│   └── pghstatic.jar         (private!)
└── static                    (files inside here are public)
    ├── index.html            - http://localhost:3000
    ├── default.htm           - http://localhost:3000
    └── media                 (no directory explorer yet)
        └── cat.jpg           - http://localhost:3000/media/cat.jpg
```

> **Note:** Only the contents of `static/` are publicly accessible with this plugin.

## Index Files

But what if there are multiple index files?

For context, an index file is the "home page" of a directory or URL. It's shown when a specific file is not specified in
the URL, and can be used to shorten links.

These are defined as a list in `indexes.txt` which can be modified. Do keep in mind, however, that this file requires a
reboot to refresh!

Each defined entry is attempted in-order when needed, so if the file looks like this...

```text
index.html
default.html
```

... `index.html` will be attempted first. Attempting an index file, in this case, means it will be located in the
current
directory and sent to the user. If none are found, a 404 Not Found page will be sent instead.