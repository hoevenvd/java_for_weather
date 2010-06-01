#JAVA_HOME=/usr/java/jdk1.5.0_05
#JAVA_HOME=/usr/jdk1.6.0_10

#PATH=$JAVA_HOME/bin:$PATH
WX_HOME=./

cd $WX_HOME

java -noverify -cp lib/spring.jar:lib/commons-logging.jar:lib/log4j.jar:classes:lib/mysql-connector-java.jar:lib/comm.jar:lib/commons-collections.jar:lib/commons-dbcp.jar:lib/commons-pool.jar:lib/jcommon.jar:lib/jfreechart.jar -Dlog4j.configuration=file://$WX_HOME/charts-log4j.properties org.tom.weather.graph.Grapher

