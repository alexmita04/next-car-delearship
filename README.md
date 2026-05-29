# Next Car Dealership

Aplicație Java pentru gestionarea unui dealership auto, cu persistență PostgreSQL și interfață grafică JavaFX.

## Rulare interfață grafică (JavaFX)

Proiectul **nu folosește Maven**. Scriptul `build.sh` descarcă automat JavaFX SDK (compatibil JDK 26) și compilează sursele.

```bash
./build.sh gui
```

Alte comenzi:

```bash
./build.sh compile   # doar compilare
./build.sh console   # demo consolă (Main.java)
```

## Configurare bază de date

Creează `src/resources/db.properties` cu URL-ul și credențialele PostgreSQL, apoi rulează `sql/schema.sql`.

## Structură UI

| Clasă | Rol |
|-------|-----|
| `ui.MainView` | Fereastra principală cu meniu (Inventar / Vânzări / Clienți) |
| `ui.InventoryView` | Tabel inventar + toolbar (Adaugă / Șterge / Editează / Caută) |
| `ui.AddCarDialog` | Formular adăugare mașină (nouă / second-hand) |
| `ui.SaleView` | Listă vânzări + înregistrare / anulare vânzare |
| `ui.ClientView` | Gestionare clienți |
