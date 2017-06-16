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

ant_cmd = "ant -silent -buildfile #{build_dir}/build.xml -propertyfile #{build_dir}/config/version.properties -propertyfile #{build_dir}/config/build.properties -propertyfile #{build_dir}/build.number"

console.append "Preparing to build Charcoal\n"

puts 'Cleaning environment'
unless run_cmd "#{ant_cmd} clean"
  return
end

puts 'Compiling protocol buffers'
unless run_cmd "#{ant_cmd} compile.proto"
  return
end

puts 'Compiling project'
unless run_cmd "#{ant_cmd} compile.java"
  return
end

puts 'Building charcoal'
charcoal_cp = resolve_classpath(project_dir, 'X')
unless run_cmd "#{ant_cmd} -Dcharcoal.cp=#{Shellwords.escape(charcoal_cp)} jar.charcoal"
  return
end

FileUtils.rm_rf("#{config['OUTPUT_DIR']}")
FileUtils.mkdir_p "#{config['OUTPUT_DIR']}/lib/java"

# copy libraries
charcoal_cp.split(" ").each { |lib|
  FileUtils.cp("#{config['PROJECT_DIR']}/#{lib}", "#{config['OUTPUT_DIR']}/lib/java")
}
FileUtils.cp("#{build_dir}/output/Charcoal.jar", "#{config['OUTPUT_DIR']}")

puts("\n#{GREEN}Built successfully in: %.1f seconds" % (Time.now - t1))
puts "Output size: @.2f MB#{RESET}" % (File.size("#{ARGV[1]}/Charcoal.jar").to_f / 2**20).round(2)
