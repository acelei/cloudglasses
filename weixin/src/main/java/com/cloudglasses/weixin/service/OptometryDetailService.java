package com.cloudglasses.weixin.service;

import com.cloudglasses.model.OptometryDetail;
import com.cloudglasses.model.WeixinUser;
import com.cloudglasses.repository.OptometryDetailRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Set;

@Service
public class OptometryDetailService {
    @Autowired
    private OptometryDetailRepository optometryDetailRepository;


    public String getOptometryFormByMobile(String mobile) {
        OptometryDetail optometryDetail = optometryDetailRepository.findFirstByMobileAndStatusTrueOrderByCreateTimeDesc(mobile);
        return getOptometryForm(optometryDetail);
    }

    public OptometryDetail findLastDetail(WeixinUser user) {
        return findLastDetail(user.getOptometryDetails());
    }

    public OptometryDetail findLastDetail(Set<OptometryDetail> optometryDetails) {
        if (optometryDetails != null) {
            return optometryDetails.stream()
                    .filter(OptometryDetail::getStatus)
                    .max(Comparator.comparing(OptometryDetail::getCreateTime))
                    .orElse(null);
        }
        return null;
    }

    public String getOptometryForm(WeixinUser user) {
        return getOptometryForm(findLastDetail(user));
    }


    public String getOptometryForm(OptometryDetail optometryDetail) {
        StringBuilder sb = new StringBuilder();
        if (optometryDetail != null) {
            sb.append("验光单\n");

            sb.append("右眼:\n");
            sb.append("    镜面度数:").append(String.format("% 10.2f", optometryDetail.getSphR() == null ? 0f : optometryDetail.getSphR())).append("\n");
            sb.append("    散光度数:").append(String.format("% 10.2f", optometryDetail.getCylR() == null ? 0f : optometryDetail.getCylR())).append("\n");
            sb.append("    散光角度:").append(String.format("% 10d", optometryDetail.getAxisR() == null ? 0 : optometryDetail.getAxisR())).append("\n");

            sb.append("左眼:\n");
            sb.append("    镜面度数:").append(String.format("% 10.2f", optometryDetail.getSphL() == null ? 0f : optometryDetail.getSphL())).append("\n");
            sb.append("    散光度数:").append(String.format("% 10.2f", optometryDetail.getCylL() == null ? 0f : optometryDetail.getCylL())).append("\n");
            sb.append("    散光角度:").append(String.format("% 10d", optometryDetail.getAxisL() == null ? 0 : optometryDetail.getAxisL())).append("\n");

            if (optometryDetail.getPd() != null) {
                sb.append("瞳距:").append(String.format("% 6d", optometryDetail.getPd())).append("\n");
            }

            if (!StringUtils.isEmpty(optometryDetail.getAddress())) {
                sb.append("验光地点:").append(optometryDetail.getAddress()).append("\n");
            }
        }

        return sb.toString();
    }
}
