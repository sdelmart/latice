## Latice @VERSION@

Jeu de société Latice en JavaFX : mode 2 joueurs ou solo contre une IA, aide au placement, sauvegarde et reprise de partie, historique, thèmes et sons. Voir le [CHANGELOG](https://github.com/sdelmart/latice/blob/main/CHANGELOG.md).

**Aucune installation de Java n'est nécessaire** : chaque archive contient l'application et son propre environnement d'exécution.

### Télécharger

| Système | Fichier |
|---|---|
| **Windows** (x64) | `Latice-@VERSION@-windows-x64.zip` (ou `.tar.gz`, ou l'installeur `.msi` s'il est présent) |
| **macOS Apple Silicon** (M1 et plus) | `Latice-@VERSION@-macos-arm64.tar.gz` (ou `.dmg`) |
| **macOS Intel** | `Latice-@VERSION@-macos-x64.tar.gz` (ou `.dmg`) |
| **Linux x86-64** | `Latice-@VERSION@-linux-x64.tar.gz` (ou `.deb`) |
| **Linux ARM64** | `Latice-@VERSION@-linux-arm64.tar.gz` (ou `.deb`) |

`SHA256SUMS.txt` contient les sommes de contrôle de tous les fichiers :
`sha256sum -c SHA256SUMS.txt --ignore-missing`.

### Lancer

- **Windows** : extraire l'archive, lancer `Latice\Latice.exe`. Windows SmartScreen peut afficher un avertissement car l'application n'est pas signée par un éditeur certifié : *Informations complémentaires → Exécuter quand même*.
- **macOS** : extraire l'archive (ou ouvrir le `.dmg` et glisser `Latice` dans Applications). L'application n'est pas notarisée par Apple, donc au premier lancement : *clic droit sur Latice → Ouvrir → Ouvrir*, ou dans un terminal `xattr -dr com.apple.quarantine Latice.app`.
- **Linux** : `tar -xzf Latice-@VERSION@-linux-*.tar.gz && ./Latice/bin/Latice`. JavaFX a besoin de GTK 3 et d'ALSA, déjà présents sur la plupart des distributions de bureau (`sudo apt install libgtk-3-0 libasound2` sinon). Le `.deb` s'installe avec `sudo apt install ./Latice-*.deb`.

Les données (historique des parties, partie sauvegardée) sont stockées dans `~/.latice` (dossier utilisateur).
