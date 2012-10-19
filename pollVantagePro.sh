#WX_HOME=/home/tom/poller
#JAVA_HOME=/usr/jdk1.6.0_10

PATH=$JAVA_HOME/bin:$PATH
TZ=America/New_York

#cd $WX_HOME

#cp -f jweatherstation.properties.vp jweatherstation.properties
#cp -f weather.xml.vp weather.xml

while true 
do
  java -Duser.timezone=$TZ -noverify -cp .:lib/spring.jar:lib/commons-logging.jar:lib/log4j.jar:classes:lib/mysql-connector-java.jar:lib/comm.jar:lib/commons-collections.jar:lib/commons-dbcp.jar:lib/commons-pool.jar:lib/jcommon.jar:lib/jfreechart.jar:lib/axis.jar:lib/jaxrpc.jar:lib/commons-discovery-0.2.jar:lib/saaj.jar:lib/wsdl4j-1.5.1.jar:lib/gson-1.7.1.jar org.tom.weather.comm.Main
  sleep 30
done
