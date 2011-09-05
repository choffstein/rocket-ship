# rocket-ship

rocket-ship is a library for deploying my .war files to Amazon's EC2.

Huge thanks to hugod in irc.freenode.net #pallet for spending so much time helping me to get this working.

## Usage

Make sure you have a config.clj file like the one below.  I auto-build & deploy with jenkins on MacOSX, so I created a special ssh key (no passphrase) in my jenkins' user home director.
This config.clj file should also be in the .pallet directory of /Users/jenkins.

Make sure you fill in the identity and credential parts with your AWS key and secret-key.

## ~/.pallet/config.clj

    (defpallet
        :services
            {:aws-ec2 {:provider "aws-ec2"
                       :identity ""
                       :credential ""}}
        :environment {:user {:username "jenkins"
                      :public-key-path "/Users/jenkins/.ssh/ec2_rsa.pub"
		      :private-key-path "/Users/jenkins/.ssh/ec2_rsa"}})

## Other

Please feel free to fork, fix, and pull for something as minor as code style to as major as
a global-meltdown of a bug.

## License

Copyright (C) 2011 Newfound Research, LLC

Distributed under the Eclipse Public License, the same as Clojure.
