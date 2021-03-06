/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *      http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 *                                                                            *
 *****************************************************************************/
package com.subterranean_security.crimson.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.junit.Test;

public class CertUtilTest {

	public static final String testCert = "Q2VydGlmaWNhdGU6CiAgICBEYXRhOgogICAgICAgIFZlcnNpb246IDMgKDB4MikKICAgICAgICBTZXJpYWwgTnVtYmVyOiAxICgweDEpCiAgICBTaWduYXR1cmUgQWxnb3JpdGhtOiBzaGExV2l0aFJTQUVuY3J5cHRpb24KICAgICAgICBJc3N1ZXI6IEM9VVMsIFNUPVRleGFzLCBMPUF1c3RpbiwgTz1TdWJ0ZXJyYW5lYW4gU2VjdXJpdHksIENOPVN1YnRlcnJhbmVhbiBTZWN1cml0eSBSb290IENBL2VtYWlsQWRkcmVzcz1hZG1pbkBzdWJ0ZXJyYW5lYW4tc2VjdXJpdHkuY29tCiAgICAgICAgVmFsaWRpdHkKICAgICAgICAgICAgTm90IEJlZm9yZTogTWF5IDI3IDA1OjAxOjIxIDIwMTcgR01UCiAgICAgICAgICAgIE5vdCBBZnRlciA6IE1heSAyNyAwNTowMToyMSAyMDE4IEdNVAogICAgICAgIFN1YmplY3Q6IEM9VVMsIFNUPVRleGFzLCBPPVN1YnRlcnJhbmVhbiBTZWN1cml0eSwgT1U9U3VidGVycmFuZWFuIFNlY3VyaXR5LCBDTj1kZWJ1Zy9lbWFpbEFkZHJlc3M9YWRtaW5Ac3VidGVycmFuZWFuLXNlY3VyaXR5LmNvbQogICAgICAgIFN1YmplY3QgUHVibGljIEtleSBJbmZvOgogICAgICAgICAgICBQdWJsaWMgS2V5IEFsZ29yaXRobTogcnNhRW5jcnlwdGlvbgogICAgICAgICAgICAgICAgUHVibGljLUtleTogKDIwNDggYml0KQogICAgICAgICAgICAgICAgTW9kdWx1czoKICAgICAgICAgICAgICAgICAgICAwMDphZjo4OTo5ZDozYzpjNjo3YTo1Mzo5ZjoxYjo1ZDpmODo2MzoyNTo3MjoKICAgICAgICAgICAgICAgICAgICA3MzowOTpkYjoyYzo2Nzo2MTpmMjpkODpiYTowZTpiMjplNTpkMjpiNTozNToKICAgICAgICAgICAgICAgICAgICBmYjpkMTpkYzphYjo5Yjo2YjozMzo0NTpiMzo2NTpiYjoyZTo0NzpjMTphNjoKICAgICAgICAgICAgICAgICAgICAyMTo1Nzo4Mjo5Njo3ZDozMjo3YjoxYzphMTo5MTo5YjphZDpjYjoxZDoyZDoKICAgICAgICAgICAgICAgICAgICA5OTplMToxZTo4NDowNzowMzoyYToyMzozODo1Nzo2OTowOTphYzoyYzpkNjoKICAgICAgICAgICAgICAgICAgICA0Yjo4Yzo1YTo2ZToyNTo2NDpmYjo0NDpjOTo1MToyZjo4MToyZjo5ZTo3MToKICAgICAgICAgICAgICAgICAgICBmNDpkNjozMDo1Yzo0NToxYjpjYzo4NTo2NzoxODpjYTplODo5MDo5MzpkMDoKICAgICAgICAgICAgICAgICAgICBlMTo1NDoyOTpmYjo0ZTo4NzoyMzo2Zjo2MTo2ZjpkYzphZjoxNzozZDo4YzoKICAgICAgICAgICAgICAgICAgICBiNzo3Yjo2YTphNjpkYjpiZDplZTo0MTo5NjpkOTozMDo5YTo1MTozZTplNDoKICAgICAgICAgICAgICAgICAgICA5MDo2NjplOTpmMzo0NTphOTo3NTo1ZToxYTo0NjowMjpmZTo0ODoyZDphYjoKICAgICAgICAgICAgICAgICAgICBjYjowZDo5Yzo2NjpkMzpmZjpmNTo5ZTo0MzozNTpmMjo4YjphMTpiYzozYzoKICAgICAgICAgICAgICAgICAgICBjNTpjNjpmZjpmNzoxMjoyMzozNzo4MzoyNjplOToxZTo4Yjo0ZDplYjowNToKICAgICAgICAgICAgICAgICAgICBhZToxOTpkZTo2MTowZDoyYzo3Njo5Yjo2MDpjNDo4NTowYzo2ZTpjNjo5ODoKICAgICAgICAgICAgICAgICAgICA5ZjowMzoyYTpjMTozZjo2ZTpiOTowNDpiNTo5Njo5Mzo3MDpmNjpiMjo3NDoKICAgICAgICAgICAgICAgICAgICA5YToyZDo3MTpiZjozOToyNjpjNDo0NzpkYjo4NTo5MDpkNzoxZToxYzo5MDoKICAgICAgICAgICAgICAgICAgICBkNTplNTo4ZDozYjplMjphNzoyNzo4ZTozNzoyNDowZToyNjo5ODoxMTo0NDoKICAgICAgICAgICAgICAgICAgICA2NDpmYzo1MjpjYTpmOToxMzo5NTowMjo5Zjo2NjpiNTpjYTpkYjo2Yzo3NDoKICAgICAgICAgICAgICAgICAgICBlZjphNQogICAgICAgICAgICAgICAgRXhwb25lbnQ6IDY1NTM3ICgweDEwMDAxKQogICAgICAgIFg1MDl2MyBleHRlbnNpb25zOgogICAgICAgICAgICBYNTA5djMgQmFzaWMgQ29uc3RyYWludHM6IAogICAgICAgICAgICAgICAgQ0E6RkFMU0UKICAgICAgICAgICAgWDUwOXYzIFN1YmplY3QgS2V5IElkZW50aWZpZXI6IAogICAgICAgICAgICAgICAgMjA6OUY6Njg6OTg6QUU6RkM6ODI6M0I6MjY6OTk6QUU6Njc6NkE6Mjk6NUI6NDQ6REE6MjA6NzI6NDcKICAgICAgICAgICAgWDUwOXYzIEF1dGhvcml0eSBLZXkgSWRlbnRpZmllcjogCiAgICAgICAgICAgICAgICBrZXlpZDowQjo0MTo0MTo4NjpGQjo0MTozNDozRTpGQzpDRDpCODozOTpFNTozNTo5Mjo1RDo5Njo0ODozNTo2MwogICAgICAgICAgICAgICAgRGlyTmFtZTovQz1VUy9TVD1UZXhhcy9MPUF1c3Rpbi9PPVN1YnRlcnJhbmVhbiBTZWN1cml0eS9DTj1TdWJ0ZXJyYW5lYW4gU2VjdXJpdHkgUm9vdCBDQS9lbWFpbEFkZHJlc3M9YWRtaW5Ac3VidGVycmFuZWFuLXNlY3VyaXR5LmNvbQogICAgICAgICAgICAgICAgc2VyaWFsOkIxOjVGOjZEOkM4OjdDOjc1OkUzOjEzCgogICAgICAgICAgICBOZXRzY2FwZSBDQSBSZXZvY2F0aW9uIFVybDogCiAgICAgICAgICAgICAgICBodHRwczovL3d3dy5leGFtcGxlLmNvbS9leGFtcGxlLWNhLWNybC5wZW0KICAgIFNpZ25hdHVyZSBBbGdvcml0aG06IHNoYTFXaXRoUlNBRW5jcnlwdGlvbgogICAgICAgICA0Mzo5MTpjZjo3MDo5MDo1NjoyMDo5MDpmZjplYjplMTo5NjozOTplMjowZDo4ODo0ODo4ZDoKICAgICAgICAgOTA6ZWY6NjA6Zjc6ZGY6NjQ6ZTA6NTg6MGM6NTU6MGU6MjI6MzU6OWQ6YzA6ZTI6MDU6M2I6CiAgICAgICAgIDNmOjA5OmRkOmE3Ojk1OjE4OjYyOjgxOmFlOjcwOmQ4OjA3OjJiOjM5Ojg3Ojc3OmY4OjFmOgogICAgICAgICA5YTpmZTpkNzo3NDo1NzpmYTpkMzo0Yjo0YzozMDpmMjphMjo4NDo0YjpjMTphMDo1YTozNDoKICAgICAgICAgNzA6OGE6ZGM6YWM6ZGQ6ZTQ6ZDM6NjY6ZjE6NTE6YmU6NTc6Mjk6NDk6Y2I6YTg6ZDI6NGM6CiAgICAgICAgIDFjOjE1OmUxOmZhOmI2OjFlOjVjOjg3OmVjOjMwOjIyOmY0OjQ1OmY0OmIyOjY0OjY0OjY3OgogICAgICAgICBlYjoxNDo5OTplNzpjYzo5Yzo3ODpjZDowMzpiOTpjOTo2MzoyMzo3NDpiMzozODplMjpiNDoKICAgICAgICAgMjA6MzQ6YmM6NjU6YTM6ZmE6NDU6NjE6Njk6ZTM6YTc6Y2I6OGM6YmQ6NDc6ZmU6MTY6MWU6CiAgICAgICAgIDA3OmVlOmFlOmEwOmZiOjRjOmU0OjJjOjM2OjAzOjY1OmFmOmM1OjdmOjczOmEyOjI5Ojg1OgogICAgICAgICAyODozZjowYjo3ODpmYToxNzo0Nzo3NTo0Yjo5YTpiNzo0ZDpjNjpiZTozZjplZDo5MjozNzoKICAgICAgICAgM2M6M2E6ZmY6YmE6Mzg6YTM6NmM6ODU6M2U6YjA6ZDE6OWY6ZTY6YzA6ODI6NTY6ZmY6NTY6CiAgICAgICAgIDRlOmY4OjFhOmE3OmY0OmVmOmQ4OjQ5OjFmOjRiOmEzOmYwOmVjOjhiOmNhOjQ2Ojk5OmFiOgogICAgICAgICA5Zjo3Njo0NDoyODozYzpjMzpjMTozZTpiZDo0NTpmNjpkNzoxYzo2NDozNzo0YTpmYjo2ZjoKICAgICAgICAgNzI6YzY6NTc6ZGM6Mjc6ZDI6NmE6YTc6OTk6ZWU6NGQ6NjQ6YzM6YjY6NTg6ZjQ6Mjc6YTk6CiAgICAgICAgIDhkOjc5OmQwOjQ0Ci0tLS0tQkVHSU4gQ0VSVElGSUNBVEUtLS0tLQpNSUlGQ1RDQ0EvR2dBd0lCQWdJQkFUQU5CZ2txaGtpRzl3MEJBUVVGQURDQnBqRUxNQWtHQTFVRUJoTUNWVk14CkRqQU1CZ05WQkFnTUJWUmxlR0Z6TVE4d0RRWURWUVFIREFaQmRYTjBhVzR4SGpBY0JnTlZCQW9NRlZOMVluUmwKY25KaGJtVmhiaUJUWldOMWNtbDBlVEVtTUNRR0ExVUVBd3dkVTNWaWRHVnljbUZ1WldGdUlGTmxZM1Z5YVhSNQpJRkp2YjNRZ1EwRXhMakFzQmdrcWhraUc5dzBCQ1FFV0gyRmtiV2x1UUhOMVluUmxjbkpoYm1WaGJpMXpaV04xCmNtbDBlUzVqYjIwd0hoY05NVGN3TlRJM01EVXdNVEl4V2hjTk1UZ3dOVEkzTURVd01USXhXakNCblRFTE1Ba0cKQTFVRUJoTUNWVk14RGpBTUJnTlZCQWdNQlZSbGVHRnpNUjR3SEFZRFZRUUtEQlZUZFdKMFpYSnlZVzVsWVc0ZwpVMlZqZFhKcGRIa3hIakFjQmdOVkJBc01GVk4xWW5SbGNuSmhibVZoYmlCVFpXTjFjbWwwZVRFT01Bd0dBMVVFCkF3d0ZaR1ZpZFdjeExqQXNCZ2txaGtpRzl3MEJDUUVXSDJGa2JXbHVRSE4xWW5SbGNuSmhibVZoYmkxelpXTjEKY21sMGVTNWpiMjB3Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRQ3ZpWjA4eG5wVApueHRkK0dNbGNuTUoyeXhuWWZMWXVnNnk1ZEsxTmZ2UjNLdWJhek5GczJXN0xrZkJwaUZYZ3BaOU1uc2NvWkdiCnJjc2RMWm5oSG9RSEF5b2pPRmRwQ2F3czFrdU1XbTRsWlB0RXlWRXZnUytlY2ZUV01GeEZHOHlGWnhqSzZKQ1QKME9GVUtmdE9oeU52WVcvY3J4YzlqTGQ3YXFiYnZlNUJsdGt3bWxFKzVKQm02Zk5GcVhWZUdrWUMva2d0cThzTgpuR2JULy9XZVF6WHlpNkc4UE1YRy8vY1NJemVESnVrZWkwM3JCYTRaM21FTkxIYWJZTVNGREc3R21KOERLc0UvCmJya0V0WmFUY1BheWRKb3RjYjg1SnNSSDI0V1ExeDRja05YbGpUdmlweWVPTnlRT0pwZ1JSR1Q4VXNyNUU1VUMKbjJhMXl0dHNkTytsQWdNQkFBR2pnZ0ZITUlJQlF6QUpCZ05WSFJNRUFqQUFNQjBHQTFVZERnUVdCQlFnbjJpWQpydnlDT3lhWnJtZHFLVnRFMmlCeVJ6Q0Iyd1lEVlIwakJJSFRNSUhRZ0JRTFFVR0crMEUwUHZ6TnVEbmxOWkpkCmxrZzFZNkdCcktTQnFUQ0JwakVMTUFrR0ExVUVCaE1DVlZNeERqQU1CZ05WQkFnTUJWUmxlR0Z6TVE4d0RRWUQKVlFRSERBWkJkWE4wYVc0eEhqQWNCZ05WQkFvTUZWTjFZblJsY25KaGJtVmhiaUJUWldOMWNtbDBlVEVtTUNRRwpBMVVFQXd3ZFUzVmlkR1Z5Y21GdVpXRnVJRk5sWTNWeWFYUjVJRkp2YjNRZ1EwRXhMakFzQmdrcWhraUc5dzBCCkNRRVdIMkZrYldsdVFITjFZblJsY25KaGJtVmhiaTF6WldOMWNtbDBlUzVqYjIyQ0NRQ3hYMjNJZkhYakV6QTUKQmdsZ2hrZ0JodmhDQVFRRUxCWXFhSFIwY0hNNkx5OTNkM2N1WlhoaGJYQnNaUzVqYjIwdlpYaGhiWEJzWlMxagpZUzFqY213dWNHVnRNQTBHQ1NxR1NJYjNEUUVCQlFVQUE0SUJBUUJEa2M5d2tGWWdrUC9yNFpZNTRnMklTSTJRCjcyRDMzMlRnV0F4VkRpSTFuY0RpQlRzL0NkMm5sUmhpZ2E1dzJBY3JPWWQzK0IrYS90ZDBWL3JUUzB3dzhxS0UKUzhHZ1dqUndpdHlzM2VUVFp2RlJ2bGNwU2N1bzBrd2NGZUg2dGg1Y2grd3dJdlJGOUxKa1pHZnJGSm5uekp4NAp6UU81eVdNamRMTTQ0clFnTkx4bG8vcEZZV25qcDh1TXZVZitGaDRIN3E2ZyswemtMRFlEWmEvRmYzT2lLWVVvClB3dDQraGRIZFV1YXQwM0d2ai90a2pjOE92KzZPS05zaFQ2dzBaL213SUpXLzFaTytCcW45Ty9ZU1I5TG8vRHMKaThwR21hdWZka1FvUE1QQlByMUY5dGNjWkRkSysyOXl4bGZjSjlKcXA1bnVUV1REdGxqMEo2bU5lZEJFCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K";

	@Test
	public void testParseCertificateString() {
		try {
			X509Certificate output = CertUtil.parseCertificate(testCert);

			assertEquals(new Date(1495861281000L), output.getNotBefore());
			assertEquals(new Date(1527397281000L), output.getNotAfter());
		} catch (IOException | CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCertificateToStringString() {
		try {
			String output = CertUtil.certificateToString(testCert);
			System.out.println(output);
		} catch (CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

	}

}
