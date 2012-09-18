WtfCode
=======

WTF is WtfCode
--------------

WTF Code is an implementation of forum for unusual source code
discussion. The forum is focused on experienced programmers.

Ideology
--------

It is by programmers and for programmers. Programming attributes and
jokes are awesome, let's do more of those! We also respect nice
design.

Test Installation
-----------------

You can make some test-drive on
[test installation](http://wtfcode-ratvier.rhcloud.com/).

API
---

Some basic Json API is available:

`/post/by-id/<postId>.json` - extract post by post id.

`/post/by-author/<authorId>.json` - extract list of posts by author id.

`/post/<postId>/comments.json` - extract list of comments by post id.

Hacking
-------

The project is based on [LiftWeb framework](http://liftweb.net/). You
are always welcome to join.

To try current version in your local environemt, clone the repository
and execute the following command (assume you have
[maven](http://maven.apache.org/) installed and it is in your path):

    mvn jetty:run

This command will start jetty server on 8080 port. Empty h2 database
will be created on start so you will be able to create users, posts
and comments.

If you like [Sbt](http://www.scala-sbt.org/), you can build the project
with it: just execute the following command in sbt console:

    container:start

Happy hacking!


