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

require 'shellwords'

t1 = Time.now

project_dir = ARGV[0]
build_dir = "#{project_dir}/build"
output_dir = ARGV[1]

ant_cmd = "ant -silent -buildfile #{build_dir}/build.xml -propertyfile #{build_dir}/build.number"

# read version
version = File.read("#{build_dir}/config/version.properties").partition('=').last.chomp

# read build number
build_number = File.read("#{build_dir}/build.number").partition('=').last.chomp.to_i

puts "Preparing to build: Crimson #{version} (#{build_number})\n"

puts 'Compiling protocol buffers'
unless run_cmd "#{ant_cmd} compile.proto"
  exit 1
end

puts 'Compiling project'
unless run_cmd "#{ant_cmd} compile.crimson"
  exit 1
end

puts 'Copying resource tree'
unless run_cmd "#{ant_cmd} copy.resources"
  exit 1
end

puts 'Packaging core'
unless run_cmd "#{ant_cmd} jar.core"
  exit 1
end

puts 'Packaging hcp'
unless run_cmd "#{ant_cmd} jar.hcp"
  exit 1
end

puts 'Packaging sv'
unless run_cmd "#{ant_cmd} jar.sv"
  exit 1
end

puts 'Packaging cv'
unless run_cmd "#{ant_cmd} jar.cv"
  exit 1
end

puts 'Building client'
client_cp = resolve_classpath(project_dir, 'C')
unless run_cmd "#{ant_cmd} -Dclient.cp=#{Shellwords.escape(client_cp)} jar.client"
  exit 1
end

puts 'Building server'
server_cp = resolve_classpath(project_dir, 'S')
unless run_cmd "#{ant_cmd} -Dserver.cp=#{Shellwords.escape(server_cp)} jar.server"
  exit 1
end

puts 'Building viewer'
viewer_cp = resolve_classpath(project_dir, 'V')
client_lib_size = resolve_lib_size(project_dir, "#{project_dir}/lib/java", 'C')
unless run_cmd "#{ant_cmd} -Dviewer.cp=#{Shellwords.escape(viewer_cp)} -Dclient-lib.size=#{client_lib_size} jar.viewer"
  exit 1
end

puts 'Building installer'
unless run_cmd "#{ant_cmd} jar.cinstaller"
  exit 1
end

puts "\n#{GREEN}Build completed in: %.1f seconds" % (Time.now - t1)
puts "Output size: %.2f MB#{RESET}" % (File.size("#{output_dir}/CInstaller.jar").to_f / 2**20).round(2)
exit 0
