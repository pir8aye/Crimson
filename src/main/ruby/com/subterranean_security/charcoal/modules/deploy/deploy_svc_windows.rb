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

require 'timeout'
require 'rubygems'
require 'net/ssh'
require 'net/scp'

def deploy_svc_windows(config, console)
  console.append_indeterminate "Contacting: #{config['IP']}"

  begin
    ssh = Net::SSH.start(config['IP'], config['USER'])

    console.append_indeterminate "Resetting target environment"

    # kill any lingering processes
    ssh.exec!("taskkill /f /im javaw.exe")
    ssh.exec!("taskkill /f /im java.exe")
    sleep 1

    # remove registry keys
    ssh.exec!("reg delete HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v client.jar /f")
    ssh.exec!("reg delete HKCU\\Software\\JavaSoft\\Prefs /f")

    # reset filesystem
    ssh.exec!("rmdir /S /Q #{config['INSTALL_DIR']}")
    ssh.exec!("rmdir /S /Q \"C:\\Users\\%USERNAME%\\AppData\\Local\\Subterranean Security\"")
    ssh.exec!("mkdir #{config['INSTALL_DIR']}")

    # upload installer
    console.append_indeterminate "Uploading Crimson"

    if(config['IP'] == '127.0.0.1')
      # avoid SCP if localhost
      FileUtils.cp(config['DIST_DIR'] + 'CInstaller.jar', config['INSTALL_DIR'] + "\\CInstaller.jar")
    else
      Net::SCP.upload!(config['IP'], config['USER'],
      config['DIST_DIR'] + 'CInstaller.jar', config['INSTALL_DIR'] + "\\CInstaller.jar")
    end

    # run installer
    console.append_indeterminate "Installing"

    ssh.exec!("java -jar #{config['INSTALL_DIR']}\\CInstaller.jar install #{config['INSTALL_DIR']}")

    ssh.close
    console.append "\nLaunch complete. Press ENTER to begin debugging or press 'b' to run in the background"
    return true
  rescue Timeout::Error
    console.append "Operation failed (connection timeout)"
  rescue Errno::EHOSTUNREACH
    console.append "Operation failed (host unreachable)"
  rescue Errno::ECONNREFUSED
    console.append "Operation failed (connection refused)"
  rescue Net::SSH::AuthenticationFailed
    console.append "Operation failed (authentication failed)"
  end
  return false
end

def start_svc_windows(config, con_server, con_viewer, con_client)

  # launch server
  #  Thread.new{
  #    Net::SSH.start(config['IP'], config['USER']) do |ssh|
  #      ssh.exec!("javaw -jar #{config['INSTALL_DIR']}/Crimson-Server.jar")
  #    end
  #  }

  # tail the server log
  #  Thread.new {
  #    sleep 3
  #    Net::SSH.start(config['IP'], config['USER']).exec!("tail -f #{config['INSTALL_DIR']}/var/log/server") do |ch, stream, line|
  #      con_server.append(line)
  #    end
  #  }

  # launch viewer
  Thread.new{
    Net::SSH.start(config['IP'], config['USER']) do |ssh|
      ssh.exec!("RunInSession 1 javaw -jar -Ddebug.prefill.username=admin -Ddebug.prefill.password=default -Ddebug.prefill.address=127.0.0.1 -Ddebug.prefill.port=10101 #{config['INSTALL_DIR']}/Crimson-Viewer.jar")
    end
  }

  # tail the viewer log
  #  Thread.new {
  #    sleep 3
  #    Net::SSH.start(config['IP'], config['USER']).exec!("tail -f #{config['INSTALL_DIR']}/var/log/viewer") do |ch, stream, line|
  #      con_viewer.append(line)
  #    end
  #  }
end

def background_svc_windows

end

