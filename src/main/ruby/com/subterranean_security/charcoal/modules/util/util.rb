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
require 'nokogiri'

# Color codes
BLACK = "\u001B[30m"
RED = "\u001B[31m"
GREEN = "\u001B[32m"
YELLOW = "\u001B[33m"
BLUE = "\u001B[34m"
MAGENTA = "\u001B[35m"
CYAN = "\u001B[36m"
WHITE = "\u001B[37m"

RESET = "\u001B[0m"

def resolve_lib_size(project_dir, lib_dir, instance)
  size = 0

  xml = File.open("#{project_dir}/src/main/resources/com/subterranean_security/crimson/universal/res/Dependancies.xml") { |f|
    Nokogiri::XML(f)
  }

  xml.xpath('DependancyInfo').xpath('Lib').each do |lib|
    if lib.xpath('Requisites').text.include?(instance)
      size += File.new("#{lib_dir}/#{lib.attr('CID')}.jar").size
    end
  end

  return size
end

def resolve_classpath(project_dir, instance)
  cp = ''

  xml = File.open("#{project_dir}/src/main/resources/com/subterranean_security/crimson/universal/res/Dependancies.xml") { |f|
    Nokogiri::XML(f)
  }

  xml.xpath('DependancyInfo').xpath('Lib').each do |lib|
    if lib.xpath('Requisites').text.include?(instance)
      cp += "lib/java/#{lib.attr('CID')}.jar "
    end
  end

  return cp
end

def run_cmd(cmd)
  output = `#{cmd}`
  unless $?.success?
    puts "FAILED COMMAND: #{cmd}\n"
    puts output
    return false
  end
  return true
end