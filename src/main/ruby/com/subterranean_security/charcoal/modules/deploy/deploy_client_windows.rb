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

def deploy_windows
  @console.append "Contacting: #{@config['IP']}"

  begin
    ssh = Net::SSH.start(@config['IP'], @config['USER'])

    @console.append "Resetting environment"

    # kill any lingering processes
    ssh.exec!("taskkill /f /im javaw.exe")
    ssh.exec!("taskkill /f /im java.exe")
    sleep 2

    # remove registry keys
    ssh.exec!("reg delete HKCU\\Software\\JavaSoft\\Prefs /f")

    # reset filesystem
    ssh.exec!("rmdir /S /Q #{@config['INSTALL_DIR']}")
    ssh.exec!("mkdir #{@config['INSTALL_DIR']}")

    # upload client
    @console.append "Uploading client"
    Net::SCP.upload!(@config['IP'], @config['USER'],
    @config['CLIENT'], @config['INSTALL_DIR'] + "\\client.jar")

    # run client
    @console.append "Executing client"
    ssh.exec!("javaw -jar #{@config['INSTALL_DIR']}\\client.jar")

    ssh.exec!("#{@config['INSTALL_DIR']}\\quiet.exe javaw -jar #{@config['INSTALL_DIR']}\\client.jar")

    ssh.close
    @console.append "Launch successful"
  rescue Timeout::Error
    @console.append "Failed to connect to #{@config['IP']}:#{@config['PORT']}: connection timeout"
  rescue Errno::EHOSTUNREACH
    @console.append "Failed to connect to #{@config['IP']}:#{@config['PORT']}: host unreachable"
  rescue Errno::ECONNREFUSED
    @console.append "Failed to connect to #{@config['IP']}:#{@config['PORT']}: connection refused"
  rescue Net::SSH::AuthenticationFailed
    @console.append "Failed to connect to #{@config['IP']}:#{@config['PORT']}: authentication failed"
  end
end