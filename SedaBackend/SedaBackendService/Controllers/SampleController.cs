using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Controllers;
using System.Web.Http.OData;
using Microsoft.Azure.Mobile.Server;
using SedaBackendService.DataObjects;
using SedaBackendService.Models;

namespace SedaBackendService.Controllers
{
    public class SampleController : TableController<Sample>
    {
        protected override void Initialize(HttpControllerContext controllerContext)
        {
            base.Initialize(controllerContext);
            SedaBackendContext context = new SedaBackendContext();
            DomainManager = new EntityDomainManager<Sample>(context, Request);
        }

        // GET tables/Sample
        public IQueryable<Sample> GetAllSamples()
        {
            return Query();
        }

        // GET tables/Sample/48D68C86-6EA6-4C25-AA33-223FC9A27959
        public SingleResult<Sample> GetSample(string id)
        {
            return Lookup(id);
        }

        // PATCH tables/Sample/48D68C86-6EA6-4C25-AA33-223FC9A27959
        public Task<Sample> PatchSample(string id, Delta<Sample> patch)
        {
            return UpdateAsync(id, patch);
        }

        // POST tables/Sample
        public async Task<IHttpActionResult> PostSample(Sample item)
        {
            Sample current = await InsertAsync(item);
            return CreatedAtRoute("Tables", new { id = current.Id }, current);
        }

        // DELETE tables/Sample/48D68C86-6EA6-4C25-AA33-223FC9A27959
        public Task DeleteSample(string id)
        {
            return DeleteAsync(id);
        }
    }
}