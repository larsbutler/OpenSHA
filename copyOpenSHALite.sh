#!/bin/sh
    
ant compile
cd dist/
mv opensha-lite.jar opensha-lite-share.jar
echo "connecting to dmonelli on sun02"
scp opensha-lite-share.jar dmonelli@gemsun02.ethz.ch:/home/dmonelli
echo "connecting to oq on sun03"
scp opensha-lite-share.jar oq@gemsun03.ethz.ch:/home/oq
echo "connecting to oq on sun04"
scp opensha-lite-share.jar oq@gemsun04.ethz.ch:/home/oq

          








                                                                                                                                               




          

                                              



                     





                                                                                                                            






                        
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     
