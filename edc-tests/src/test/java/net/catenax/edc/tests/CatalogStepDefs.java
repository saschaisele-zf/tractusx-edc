/*
 *  Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Mercedes-Benz Tech Innovation GmbH - Initial API and Implementation
 *
 */

package net.catenax.edc.tests;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.catenax.edc.tests.data.ContractOffer;
import org.junit.jupiter.api.Assertions;

public class CatalogStepDefs {

  private List<ContractOffer> lastRequestedOffers;

  @When("'{connector}' requests the catalog from '{connector}'")
  public void requestCatalog(Connector sender, Connector receiver) throws IOException {

    final DataManagementAPI dataManagementAPI = sender.getDataManagementAPI();
    final String receiverIdsUrl = receiver.getEnvironment().getIdsUrl() + "/data";

    lastRequestedOffers =
        dataManagementAPI.requestCatalogFrom(receiverIdsUrl).collect(Collectors.toList());
  }

  @Then("the catalog contains the following offers")
  public void verifyCatalog(DataTable table) {
    for (Map<String, String> map : table.asMaps()) {
      final String sourceContractDefinitionId = map.get("source definition");
      final String assetId = map.get("asset");

      final boolean isInCatalog =
          lastRequestedOffers.stream()
              .anyMatch(
                  c ->
                      c.getAssetId().equals(assetId)
                          && c.getId().startsWith(sourceContractDefinitionId));

      Assertions.assertTrue(
          isInCatalog, "The catalog does not contain the offer for asset " + assetId);
    }
  }
}
