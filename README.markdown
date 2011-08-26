# rocket-ship

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

