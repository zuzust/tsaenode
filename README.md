# TSAEnode

Share your files in a pure P2P distributed network.  
Time-Stamped Anti-Entropy protocol is used for eventual message delivery.


## About

A few days ago, I read a post on Stack Overflow where a group of people who love sunsets asked for a program to share photos of the sunsets from where they live. They all live in different cities around the world and need a program that allow each member of the group: a) publish and share their photographs, b) be aware of the list of photos published by other members, and c) download the photos of their interest.

They all pointed out the fact that the photos should always be in the owner's machine, not in the Cloud. And they all wanted to be aware of the list of photos published by the group. Equally, they did not care if any of them were not accessible for a while because the person who posted it was not connected at the time.

_TSAEnode_ aims to be a possible answer to the preceding fake scenario.

_TSAEnode_ is yet another P2P client for a distributed network with the following features:

* No machine will perform system coordination role.
* Group members will publish their photos using a local web interface. Internally, published photos will be saved in a public directory of the owner machine, accessible to the members of the group from the Internet.
* From time to time, each node will perform scheduled synchronization sessions with other nodes in the group, in order to be aware of newly published photos and their locations. This process will eventually end with all nodes sharing the same list of published photos, no matter when or how long they have been connected or disconnected.
* Each node will have its own list of published photos, built from the synchronization sessions described before.
* No matter the delays in knowing existence of newly published photos.
* Members willing to get published photos, will download them from the owning node where they are located. If the node is not available at that moment, they should try later on.

Read the [Wiki](https://github.com/zuzust/tsaenode/wiki) for further information on node operation.


## Requirements

You will need:

* JDK 1.5 or greater
* Maven 3 or greater


## Installation

The following instructions refer to UNIX-like platforms only.

Download the zipped master branch and unzip:

    $ wget https://github.com/zuzust/tsaenode/archive/master.zip
    $ unzip master.zip

Change directory to _tsaenode-master_ folder:

    $ cd tsaenode-master

Set the installation directory in _install.sh_ file (defaults to _${HOME}/Applications/tsaenode_)
and execute the installation script:

    $ ./install.sh

Finally, source your _.bashrc_ file for the environment variables to take effect:

    $ . ~/.bashrc


## Usage

There are two modes of operation: **single mode** and **simulation mode**.

In _single mode_ you start your local node, and interact with it once it joins your group of peers. This is the normal mode of operation:

    $ tsaenode
    Usage: tsaenode [-options]
    where options include:
      -h            print this help message
      -f <config>   config file path (defaults to ${TSAENODE_HOME}/tsaenode.conf)
      -r <port>     set Java RMI registry port (defaults to 1099)

In _simulation mode_ you start 4 different local nodes in their own JVM, all belonging to the same group. Once they are up and running, you interact with them in the same way as you do in single mode:

    $ tsaenode-sim

In both operating modes, you are given the same REPL to interact with nodes:

    *********************************
    Select an action:

    0: Show node profile                Show local node profile, including Id, IP address and port.
    1: Add a file                       Publish a new file within the group.
    2: Remove a file                    Unpublish the specified file.
    3: Show file index                  Show index of published files within the group.
    4: Show operation log               Show list of operations that local node is aware of.
    5: Show summary vector              Show summary vector content.
    6: Show acknowledgement vector      Show acknowledgement vector content.
    7: Run synchronization session      Start a TSAE session on demand.
    8: Run simulation                   Start a simulated session of interactions with local node.
    9: Stop simulation                  Stop running simulated session.
    10: Exit
    *********************************

Read the [Wiki](https://github.com/zuzust/tsaenode/wiki) for more information on TSAE data structures.


## Configuration

Whatever mode of operation you use, both _tsaenode.conf_ and _tsaenode-sim.conf_ configuration files have the same structure:

    # Port of local node: Used in Naming lookup operations.
    nodePort = 2010

    # Publication folder path: Ensure you have read/write permissions on it.
    pubFolderPath = ${TSAENODE_HOME}/public/${nodePort}

    # Synchronization policy:
    # 1=Unicast     Local node synchronizes with a single randomly selected peer in the group.
    # 2=Multicast   Local node synchronizes with a subset of randomly selected peers in the group.
    # 3=Broadcast   Local node synchronizes with every single peer in the group.
    policy = 2

    # Synchronization frequency i.e. 30s, 5m, 1h
    syncFreq = "5m"

    # Group nodes settings
    group {
      # To define peer nodes in the group, add them to the nodes array
      # and define their settings as the example below.

      # List of nodes in the group
      # n0  References the local node
      # ni  References the peers in the group
      nodes = [ n0 ]
      nodes = ${group.nodes} [ n1, n2, n3 ]

      # Nodes settings
      n0 { ip = "80.58.32.97", port = ${nodePort} }
      n1 { ip = "173.194.34.56", port = 2011 }
      n2 { ip = "199.59.150.39", port = 2012 }
      n3 { ip = "173.256.100.16", port = 2013 }
    }


## Contact

By Email:   carles.ml.dev at gmail dot com  
On Twitter: [@zuzudev](https://twitter.com/zuzudev)  
On Google+: [Carles Muiños](https://plus.google.com/109480759201585988691)


## License

Copyright (C) 2012 Carles Muiños

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see [&lt;http://www.gnu.org/licenses&gt;](http://www.gnu.org/licenses/).
