# Log-Sender

This little project is supposed to read messages from a file and send it to a graylog-server

## Usage
Please start graylog by `docker-compose up` and continue setting up the server. You need to create some CA for the data-node and enable at least the UDP-input once the server is available.

The password configured for the web-interface in the `.env`-file is __verysecure__.

Once the server is running, please `mvn package` the code and start it via
```shell
java -jar target/logsender-1.0-SNAPSHOT-assembly.jar -f sample-messages.txt -g localhost
```
The first argument is the file to process. The second specifies the host-name of the running graylog-server.

Specifying the filename is mandatory, the host-name defaults to "localhost" if not specified.

## Things to mention
1. This should not be considered production code. There are no metrics, and the robustness can certainly be improved, given there's more time.
2. I chose UDP for sending, as I know this runs only locally, so the network should be rather stable. It could also be a configuration-option.
3. There are some vulnerabilities in the used dependencies (guice, jackson, gelfclient), but there's either no more recent version or - in case of jackson - I tried sticking to an already used version to minimise conflicting versions.