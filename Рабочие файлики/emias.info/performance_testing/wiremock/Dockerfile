FROM wiremock/wiremock:2.35.0

WORKDIR /home/wiremock

COPY ./__files/ /home/wiremock/__files
COPY ./mappings/ /home/wiremock/mappings
COPY docker-entrypoint.sh /

EXPOSE 8080 8443
ENTRYPOINT ["/docker-entrypoint.sh"]
