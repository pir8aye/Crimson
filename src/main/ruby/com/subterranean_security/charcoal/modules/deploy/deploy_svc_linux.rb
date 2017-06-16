##############################################################################
#                                                                            #
#                    Copyright 2017 Subterranean Security                    #
#                                                                            #
#  Licensed under the Apache License, Version 2.0 (the "License");           #
#  you may not use this file except in compliance with the License.          #
#  You may obtain a copy of the License at                                   #
#                                                                            #
#      http://www.apache.org/licenses/LICENSE-2.0                            #
#                                                                            #
#  Unless required by applicable law or agreed to in writing, software       #
#  distributed under the License is distributed on an "AS IS" BASIS,         #
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  #
#  See the License for the specific language governing permissions and       #
#  limitations under the License.                                            #
#                                                                            #
##############################################################################

require_relative '../util/util'
$stdout.sync = true

require 'net/ssh'
require 'net/scp'

host = ARGV[0]
port = ARGV[1].to_i
user = ARGV[2]
install_dir = ARGV[3]
dist_dir = ARGV[4]

puts "Contacting: #{MAGENTA}#{host}#{RESET}"

ssh = Net::SSH.start(host, user, :port => port)

puts "Resetting target environment"

# kill any lingering processes
ssh.exec!("pkill -9 -f Crimson-Server.jar")
ssh.exec!("pkill -9 -f Crimson-Viewer.jar")
ssh.exec!("pkill -9 -f client.jar")

# reset filesystem
ssh.exec!("rm -rf #{install_dir}")
ssh.exec!("mkdir -p #{install_dir}/client")

# reset preferences
ssh.exec!("rm -rf ~/.java/.userPrefs")

# upload installer
puts "Uploading Crimson"

ssh.scp.upload!(dist_dir + '/CInstaller.jar', install_dir + "/CInstaller.jar")

# run installer
puts "Installing"

ssh.exec!("java -jar #{install_dir}/CInstaller.jar install #{install_dir}")

server_properties = "-Ddebug-client=true "\
"-Ddebug-client.server=\"127.0.0.1\" "\
"-Ddebug-client.port=10101 "\
"-Ddebug-client.path.linux=\"#{install_dir}/client/client.jar\" "\
"-Ddebug-client.connection_period=3000"\

puts "Launching Server"
ssh.exec("setsid java #{server_properties} -jar #{install_dir}/Crimson-Server.jar &")

sleep 1

puts "Launching Viewer"
ssh.exec("setsid export DISPLAY=:0 && export _JAVA_AWT_WM_NONREPARENTING=1 && java -jar -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true -Ddebug.prefill.username=admin -Ddebug.prefill.password=default -Ddebug.prefill.address=127.0.0.1 -Ddebug.prefill.port=10101 #{install_dir}/Crimson-Viewer.jar &")

puts "\n#{GREEN}Crimson has been deployed#{RESET}"

sleep 3

#Thread.new {
#  sleep 5
#  # install client
#  Net::SSH.start(host, user).exec!("java -jar #{install_dir}/client-installer.jar") do |ch, stream, line|
#    con_client.append(line)
#  end
#
#  sleep 9
#  con_client.append " "
#  # launch client
#  Net::SSH.start(host, user).exec!("java -jar #{install_dir}/client/client.jar") do |ch, stream, line|
#    con_client.append(line)
#  end
#}

ssh.close
exit 0
