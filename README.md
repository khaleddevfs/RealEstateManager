
"Real Estate Manager" digitalise la gestion immobilière, permettant aux agents d'accéder et de gérer des informations détaillées sur des propriétés d'exception, même hors ligne. L'application inclut la géolocalisation des biens, un moteur de recherche multi-critères, et une interface adaptative pour smartphones et tablettes. Conçue autour du Material Design, elle offre une expérience utilisateur optimale, simplifiant la consultation et la mise à jour des fiches immobilières en déplacement.


Gestion des Biens Immobiliers

- Création, édition et mise à jour des biens immobiliers avec notification après ajout, géolocalisation automatique à partir de l'adresse, et impossibilité de suppression mais possibilité de marquer comme vendu.



Géo-localisation

- Affichage des biens sur une carte pour visualiser les biens proches de la position actuelle de l'agent immobilier.


Liste des Biens immobilier

- Présentation des biens immobilier incluant nom, image, région et le prix.


Fiche détaillée d'un Bien Immobilier

- Gestion des informations détaillées pour chaque bien (type, prix, surface, nombre de pièces, description, photos, adresse, points d'intérêt, statut, agent immobilier, un carte statique de l’adresse).


simulateur de prêt immobilier

- simulateur de pret immobilier avec un bar pour choisir le taux et le calcule de mensualité 


Moteur de Recherche

- Recherche multi-critères sur l'ensemble des biens immobiliers.


Mode Hors-ligne

- Fonctionnement de l'application en mode hors-ligne avec stockage des données dans une base de données SQLite accessible via ContentProvider.


L’architecture MVVM

- L'application utilise un modèle MVVM (Model-View-ViewModel) où les ViewModels interagissent avec les Repositories pour récupérer/modifier les données, tandis que les Activités et Fragments gèrent l'affichage de l'interface utilisateur. Les services de notification et l'intégration avec l'API Google Places jouent un rôle clé dans la fonctionnalité de l'application.



