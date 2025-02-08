export default interface ProjectSiteI {
  id: number;
  projectId: number;
  name: string;
  longitude: number | null;
  latitude: number | null;
  depth: number | null;
}
