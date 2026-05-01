const checklist = [
  "Install previous release (e.g. v1.0.0) on clean machine",
  "Publish new GitHub release (e.g. v1.0.1) with latest.yml and installer",
  "Launch app and ensure update check reports available version",
  "Confirm download progress appears and reaches 100%",
  "Use restart/install button and verify app restarts to new version",
  "Test offline startup behavior and update error messaging",
  "Validate GitHub token is only used by CI (GH_TOKEN secret)"
];

console.log("Launcher update verification checklist:");
checklist.forEach((item, index) => {
  console.log(`${index + 1}. ${item}`);
});
