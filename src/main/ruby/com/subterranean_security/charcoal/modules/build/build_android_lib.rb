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

puts "Preparing to build Crimson Android library\n"

t1 = Time.now

project_dir = ARGV[0]
build_dir = "#{project_dir}/build"
output_dir = ARGV[1]

ant_cmd = "ant -silent -buildfile #{build_dir}/build.xml -propertyfile #{build_dir}/build.number"

puts 'Cleaning environment'
unless run_cmd "#{ant_cmd} clean"
  exit 1
end

puts 'Compiling protocol buffers'
unless run_cmd "#{ant_cmd} compile.proto"
  exit 1
end

puts 'Compiling project'
unless run_cmd "#{ant_cmd} compile.android-library"
  exit 1
end

puts 'Copying resource tree'
unless run_cmd "#{ant_cmd} copy.resources"
  exit 1
end

puts 'Building Android library'
unless run_cmd "#{ant_cmd} jar.android-library"
  exit 1
end

puts "\n#{GREEN}Build completed in: %.1f seconds#{RESET}" % (Time.now - t1)
exit 0