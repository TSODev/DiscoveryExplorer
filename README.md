# DiscoveryExplorer


exemple de ligne de commande: (nécessite un JVM)

**./DiscoveryExplorer -s "https://serveur/api/v1.9/" -u *username* -p *password* --host -n *nomdunoeud*  -xvc**

Il s'agit d'un programme en ligne de commande qui permet de créer un fichier pdf représentant le profil d'un Host de BMC Discovery


##Attention : 
Ce programme utilise une application externe pour générer les graphes représentants les relations sur le noeud.

Si vous souhaitez obtenir les graphes , vous devez installer cette extension sur votre poste

https://graphviz.gitlab.io/download/

Le module utilisé est **dot**. 

(Le programme vérifie la présence de l'exécutable avant de générer les graphes). En absence de dot, seules les informations détaillées du noeud sont proposées 


##Usage:
**usage**: [-h] [-v] -s SERVER [-x] -u USERNAME -p PASSWORD [--host] -n NAME [-c]


Discovery Explorer :


required arguments:

**-s SERVER**, **--server SERVER**   :         URL API du serveur Discovery , (https et termine avec
      '/') généralement https://server/api/v1.4/

**-u USERNAME**, **--username USERNAME**   :     Login - Nom de l'utilisateur


**-p PASSWORD**, **--password PASSWORD**   :      Login - Mot de passe


**-n NAME**, **--name NAME** :  nom du noeud

*le nom du fichier généré est le nom du noeud (.pdf ; . gv ; -png)*


optional arguments:


-h, --help :           affiche ce message d'aide

-v, --verbose :        valide le mode verbeux

-x, --unsecure :       ne vérifie pas le certificat SSL (utile avec des certificats auto signés)

--host, --softwareinstance :              Type de noeud (kind) (par défaut : --host)


-c, --clean :          efface les fichiers intermédiaires (.gv ; .png)