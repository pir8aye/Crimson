**Crimson** is an advanced remote control system for both casual administrators and power users. **Crimson** is in stage ALPHA, so expect problems. Most bugs should be picked up by the automatic reporter, but feel free to open an [issue](https://github.com/Subterranean-Security/Crimson/issues) with helpful information.

<p align="center">
	<img src="http://subterranean-security.com/img/github/main.png">
</p>

Official Project Page: [http://subterranean-security.com/crimson](http://subterranean-security.com/crimson)

## FAQ

```
Where can I download Crimson?
```

- The latest build can always be downloaded directly from the website: [http://subterranean-security.com/crimson](http://subterranean-security.com/crimson)<br>
- Old builds are available in the release repository: [https://github.com/Subterranean-Security/Crimson-Release](https://github.com/Subterranean-Security/Crimson-Release)<br>

```
Hey! Why should I buy a serial key when all the code is right here?
```
+ Because I believe in both free software and not eating ramen noodles for every meal. Crimson is fully usable without buying a serial key (but please consider supporting the project).


```
Why did the versioning system change?
```	
+ I have adopted a 4 number (a.b.c.d) versioning system from now on to differentiate from the old legacy versions: (e.f.g).


```
What is the history behind Crimson?
```
+ Crimson started out as a sandbox project and was eventually released in 2014.  Crimson has improved by many orders of magnitude since then, but is still a work-in-progress.  I am still the only one working on Crimson, so development can be slow. 

```
How does the future for Crimson look?
```
+ I am going to continue working on Crimson in my spare time.  I estimate I've spent at least 3000 hours on this project since the beginning, so I don't want to quit before its done.

```
Crimson looks pretty good.  Why is it so unknown?
```
+ First reason: Crimson still has quite a few bugs and is not yet ready for widespread consumption. Second reason: I **hate** marketing.

## Getting Started
+ Download the official installer and ensure you have (at least) Java 1.8 installed
+ Install Crimson using the official installer. You should install both the server and viewer components if this is your first time.  Make sure you remember the admin credentials specified during the install!
+ Launch the Crimson viewer and login to a server.  Choose "local server" if you installed the server in the previous step.
+ Generate an installer by choosing "Generator" from the main menu.
  + Create an entry in the network table for your server.  This is the address and port which the client will attempt to connect.  Crimson starts a default listener on port 10101, but any port can be used.
  + Choose an authentication type. Use "none" only in testing environments!

+ Start a listener on the port specified in the previous step if you used a value other than the default
+ Transfer the installer to the client computer and run  

## Building Crimson From Source
+ Prerequisites
  + Ant
  + protoc 3.2.0+