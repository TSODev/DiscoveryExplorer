# DiscoveryExtractor



Il s'agit d'un programme en ligne de commande qui permet de créer un fichier de données au format CSV à partir d'une commande de recherche exécutée sur un serveur BMC Discovery


**usage**: [-h] [-v] -s SERVER -u USERNAME -p PASSWORD [-q QUERY] [-o OUTPUT]



required arguments:

  **-s SERVER**, **--server SERVER**  : 
  URL API du serveur Discovery , (https et termine avec '/') généralement https://server/api/v1.1/

  **-u USERNAME**,**--username USERNAME**  :
  Login - Nom de l'utilisateur

  **-p PASSWORD**,**--password PASSWORD**  :
  Login - Mot de passe


optional arguments:

  -h, --help       :     show this help message and exit

  -v, --verbose    :     valide le mode verbeux

  **-q QUERY**,**--query QUERY**  :
  requete sur le serveur (par défaut : *search Host*)

  **-o OUTPUT**,**--output OUTPUT**  :   
  Chemin complet du fichier resultat (par défaut : *DiscoveryExtractedData.csv*)

