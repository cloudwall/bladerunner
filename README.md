## Overview

Bladerunner lets you run Dagger-configured components called Blades. Blades run on their own threads and have a basic lifecycle of initialization, 
steady state running and shutdown. It uses a combination of log4j2, Typesafe's Config library and args4j to offer a bare-minimum command line shell
that can be built on to make larger applications. Rather than a heavyweight framework, it's best to think of Bladerunner as an industrial-strength 
main() method: once your application is configured and started up everything else is left up to you.
