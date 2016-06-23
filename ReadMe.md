# Groovy LDAP API

Simple Groovy API in order to pull out and add objects to LDAP.

### Requried Environment Variables:

- LDAP_URL
- LDAP_BIND_DN
- LDAP_BIND_PASSWORD
- LDAP_SEARCH_BASE


## Search Request

GET http://localhost:9090/ldap/search?filter=(objectclass=*)

Will return the LDAP objects matching that search.

base is optional - and sill search from that searchbase.

	curl -X GET -H "Content-Type: application/json" "http://localhost:9090/ldap/search?filter=(objectclass=*)"

## Add Request

POST application/json to http://localhost:9090/ldap/add

```
{
  "add": [
  {
    "ou": "bye",
    "dn": "ou=bye,dc=example,dc=com",
    "objectclass": [
      "top",
      "organizationalUnit"
    ]
  },
  {
    "ou": "hi",
    "dn": "ou=hi,dc=example,dc=com",
    "objectclass": [
      "top",
      "organizationalUnit"
    ]
  }
  ]
}
```



## Delete Request

POST application/json to http://localhost:9090/ldap/delete


```
{
    "delete": [
            "ou=bye,dc=example,dc=com"
        ]
}
```

